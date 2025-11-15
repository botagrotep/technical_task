package technikal.task.fishmarket.services;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import technikal.task.fishmarket.dto.FishUpdateDto;
import technikal.task.fishmarket.models.Fish;
import technikal.task.fishmarket.models.FishImage;
import technikal.task.fishmarket.repasitories.FishImageRepository;

@Service
public class FishImageServiceImpl implements FishImageService {

	@Autowired
	private FishImageRepository fishImageRepository;

	@Autowired
	private FishRepository fishRepository;

	/**
	 * Saves a list of uploaded image files for a given Fish entity.
	 *
	 * This method performs the following steps: 1. Checks if the provided file list
	 * is null or empty. If so, it returns immediately. 2. Ensures that the upload
	 * directory ("public/images/") exists; if not, it creates it. 3. Iterates over
	 * each MultipartFile: - Skips empty files. - Generates a unique filename by
	 * prepending the current timestamp to the original file name. - Copies the file
	 * to the upload directory. - Creates a FishImage entity linking the file to the
	 * Fish object. 4. Saves all FishImage entities to the repository.
	 *
	 * Any exceptions during file operations are caught and printed to the console.
	 *
	 * @param fish  the Fish entity to associate the uploaded images with
	 * @param files the list of image files to save
	 */
	@Override
	public void saveFishImage(Fish fish, List<MultipartFile> files) {
		if (files == null || files.isEmpty())
			return;

		List<FishImage> imageEntities = new ArrayList<>();

		try {
			String uploadDir = "public/images/";
			Path uploadPath = Paths.get(uploadDir);

			if (!Files.exists(uploadPath)) {
				Files.createDirectories(uploadPath);
			}

			for (MultipartFile file : files) {
				if (file.isEmpty())
					continue;

				String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();

				try (InputStream inputStream = file.getInputStream()) {
					Files.copy(inputStream, uploadPath.resolve(fileName), StandardCopyOption.REPLACE_EXISTING);
				}

				FishImage img = new FishImage();
				img.setFileName(fileName);
				img.setFish(fish);
				imageEntities.add(img);
			}

			fishImageRepository.saveAll(imageEntities);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * Retrieves all images associated with a given Fish entity.
	 *
	 * This method queries the FishImage repository and returns a list of FishImage
	 * objects that are linked to the specified Fish.
	 *
	 * @param fish the Fish entity for which to fetch images
	 * @return a list of FishImage objects associated with the given Fish
	 */
	@Override
	public List<FishImage> getImagesFish(Fish fish) {
		return fishImageRepository.findByFish(fish);
	}

	/**
	 * Updates the details of an existing Fish entity.
	 *
	 * This method performs the following operations: 1. Finds the Fish entity by
	 * its ID from the provided FishUpdateDto. Throws a RuntimeException if the Fish
	 * is not found. 2. Updates the Fish's name and price based on the DTO. 3.
	 * Deletes any images specified in the DTO's imagesToDelete list: - Finds the
	 * image by its ID, throws an exception if not found. - Deletes the image file
	 * from the local storage. - Removes the image record from the repository. 4.
	 * Saves any new images provided in the DTO using the saveFishImage method. 5.
	 * Persists the updated Fish entity to the repository.
	 *
	 * @param dto the FishUpdateDto containing updated data and optional image
	 *            modifications
	 */
	@Override
	public void updateFish(FishUpdateDto dto) {

		Fish fish = fishRepository.findById(dto.getId()).orElseThrow(() -> new RuntimeException("Fish not found"));

		fish.setName(dto.getName());
		fish.setPrice(dto.getPrice());

		if (dto.getImagesToDelete() != null && !dto.getImagesToDelete().isEmpty()) {
			for (Long imageId : dto.getImagesToDelete()) {
				FishImage img = fishImageRepository.findById(imageId)
						.orElseThrow(() -> new RuntimeException("Image not found"));

				try {
					Path path = Paths.get("public/images/" + img.getFileName());
					Files.deleteIfExists(path);
				} catch (Exception e) {
					e.printStackTrace();
				}

				fishImageRepository.delete(img);
			}
		}

		if (dto.getImages() != null && !dto.getImages().isEmpty()) {
			saveFishImage(fish, dto.getImages());
		}

		fishRepository.save(fish);
	}

	/**
	 * Deletes a FishImage entity by its ID.
	 *
	 * This method performs the following operations: 1. Finds the FishImage entity
	 * by the provided imageId. Throws a RuntimeException if the image is not found.
	 * 2. Deletes the corresponding image file from local storage (if it exists). 3.
	 * Removes the FishImage record from the repository.
	 *
	 * @param imageId the ID of the FishImage to be deleted
	 */
	@Override
	public void deleteFishImage(Long imageId) {
		FishImage img = fishImageRepository.findById(imageId)
				.orElseThrow(() -> new RuntimeException("Image not found"));

		try {
			Path path = Paths.get("public/images/" + img.getFileName());
			Files.deleteIfExists(path);
		} catch (Exception e) {
			e.printStackTrace();
		}

		fishImageRepository.delete(img);
	}

}
