package technikal.task.fishmarket.controllers;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.Valid;
import technikal.task.fishmarket.models.Fish;
import technikal.task.fishmarket.models.FishDto;
import technikal.task.fishmarket.models.FishImage;
import technikal.task.fishmarket.services.FishRepository;

@Controller
@RequestMapping("/fish")
public class FishController {

	private static final String UPLOAD_DIR = "public/images/";

	@Autowired
	private FishRepository repo;

	@GetMapping({"", "/"})
	public String showFishList(Model model) {
		List<Fish> fishList = repo.findAll(Sort.by(Sort.Direction.DESC, "id"));
		model.addAttribute("fishlist", fishList);
		return "index";
	}

	@GetMapping("/create")
	public String showCreatePage(Model model) {
		model.addAttribute("fishDto", new FishDto());
		return "createFish";
	}

	@GetMapping("/delete")
	public String deleteFish(@RequestParam int id) {
		try {
			Fish fish = repo.findById(id).get();

			for (FishImage image : fish.getImages()) {
				Files.deleteIfExists(Paths.get(UPLOAD_DIR + image.getFileName()));
			}

			repo.delete(fish);
		} catch (Exception ex) {
			System.out.println("Exception: " + ex.getMessage());
		}

		return "redirect:/fish";
	}

	@PostMapping("/create")
	public String addFish(@Valid @ModelAttribute FishDto fishDto, BindingResult result) {
		if (fishDto.getImageFiles() == null || fishDto.getImageFiles().isEmpty()
				|| fishDto.getImageFiles().stream().allMatch(MultipartFile::isEmpty)) {
			result.addError(new FieldError("fishDto", "imageFiles", "Потрібне фото рибки"));
		}

		if (result.hasErrors()) {
			return "createFish";
		}

		Fish fish = new Fish();
		fish.setName(fishDto.getName());
		fish.setPrice(fishDto.getPrice());
		fish.setCatchDate(new Date());

		try {
			Path uploadPath = Paths.get(UPLOAD_DIR);
			if (!Files.exists(uploadPath)) {
				Files.createDirectories(uploadPath);
			}

			for (MultipartFile file : fishDto.getImageFiles()) {
				if (file.isEmpty()) continue;

				String storageFileName = java.util.UUID.randomUUID().toString() + "_" + file.getOriginalFilename();

				try (InputStream inputStream = file.getInputStream()) {
					Files.copy(inputStream, Paths.get(UPLOAD_DIR + storageFileName), StandardCopyOption.REPLACE_EXISTING);
				}

				FishImage image = new FishImage();
				image.setFileName(storageFileName);
				image.setFish(fish);
				fish.getImages().add(image);
			}
		} catch (Exception ex) {
			System.out.println("Exception: " + ex.getMessage());
		}

		repo.save(fish);

		return "redirect:/fish";
	}
}
