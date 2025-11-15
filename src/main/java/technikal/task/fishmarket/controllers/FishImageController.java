package technikal.task.fishmarket.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import jakarta.persistence.EntityNotFoundException;
import technikal.task.fishmarket.models.Fish;
import technikal.task.fishmarket.models.FishImage;
import technikal.task.fishmarket.services.FishImageService;
import technikal.task.fishmarket.services.FishRepository;

@Controller
@RequestMapping("/fish/images")
public class FishImageController {

	@Autowired
	private FishRepository fishRepository;

	@Autowired
	private FishImageService fishImageService;

	/**
	 * Handles HTTP POST requests to upload multiple images for a specific fish.
	 *
	 * @param fishId the ID of the fish to which the images will be associated
	 * @param files  a list of image files to be uploaded
	 * @return a redirect to the /fish page after successful upload
	 * @throws EntityNotFoundException if no fish with the given ID exists
	 */
	@PostMapping("/upload")
	public String uploadImages(@RequestParam int fishId, @RequestParam("images") List<MultipartFile> files) {
		Fish fish = fishRepository.findById(fishId).orElseThrow(() -> new EntityNotFoundException("Fish not found"));
		fishImageService.saveFishImage(fish, files);
		return "redirect:/fish";
	}

	/**
	 * Handles HTTP GET requests to display all images for a specific fish.
	 *
	 * @param fishId the ID of the fish whose images are to be displayed
	 * @param model  the Spring Model to pass data to the view
	 * @return the name of the view ("fishImages") to render the images
	 * @throws ResponseStatusException if no fish with the given ID exists (returns
	 *                                 404 Not Found)
	 *
	 *                                 This method also adds the fish's main image
	 *                                 (if it exists) at the beginning of the image
	 *                                 list.
	 */
	@GetMapping("/{fishId}")
	public String viewImages(@PathVariable int fishId, Model model) {
		Fish fish = fishRepository.findById(fishId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Fish not found"));

		List<FishImage> images = fishImageService.getImagesFish(fish);

		if (fish.getImageFileName() != null && !fish.getImageFileName().isEmpty()) {
			FishImage mainImage = new FishImage();
			mainImage.setFileName(fish.getImageFileName());
			images.add(0, mainImage);
		}

		model.addAttribute("fish", fish);
		model.addAttribute("images", images);
		return "fishImages";
	}

	/**
	 * Handles HTTP POST requests to delete a specific image of a fish.
	 *
	 * @param fishId  the ID of the fish to which the image belongs
	 * @param imageId the ID of the image to be deleted
	 * @return a redirect URL to the fish's images page after deletion
	 *
	 *         This method calls the service to delete the image both from the
	 *         database and from the file system.
	 */
	@PostMapping("/delete")
	public String deleteFishImage(@RequestParam int fishId, @RequestParam Long imageId) {
		fishImageService.deleteFishImage(imageId);
		return "redirect:/fish/images/" + fishId;
	}

}
