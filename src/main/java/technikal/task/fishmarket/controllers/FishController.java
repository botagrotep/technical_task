package technikal.task.fishmarket.controllers;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Date;
import java.util.List;

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
import technikal.task.fishmarket.services.FishRepository;

@Controller
@RequestMapping("/fish")
public class FishController {

	private final FishRepository repo;

	public FishController(FishRepository repo) {
		this.repo = repo;
	}

	@GetMapping({ "", "/" })
	public String showFishList(Model model) {
		List<Fish> fishlist = repo.findAll(Sort.by(Sort.Direction.DESC, "id"));
		model.addAttribute("fishlist", fishlist);
		return "index";
	}

	@GetMapping("/create")
	public String showCreatePage(Model model) {
		FishDto fishDto = new FishDto();
		model.addAttribute("fishDto", fishDto);
		return "createFish";
	}

	@GetMapping("/delete")
	public String deleteFish(@RequestParam int id) {

		try {

			Fish fish = repo.findById(id).orElse(null);

			if (fish != null) {
				for (technikal.task.fishmarket.models.FishImage fishImage : fish.getImages()) {
					Path imagePath = Paths.get("public/images/" + fishImage.getFileName());
					try {
						Files.deleteIfExists(imagePath);
					} catch (Exception e) {
						System.out.println("Failed to delete image: " + fishImage.getFileName());
					}
				}
				repo.delete(fish);
			}

		} catch (Exception ex) {
			System.out.println("Exception: " + ex.getMessage());
		}

		return "redirect:/fish";
	}

	@PostMapping("/create")
	public String addFish(@Valid @ModelAttribute FishDto fishDto, BindingResult result) {

		if (fishDto.getImageFiles() == null || fishDto.getImageFiles().isEmpty()
				|| fishDto.getImageFiles().get(0).isEmpty()) {
			result.addError(new FieldError("fishDto", "imageFiles", "Потрібне хоча б одне фото рибки"));
		}

		if (result.hasErrors()) {
			return "createFish";
		}

		Fish fish = new Fish();
		fish.setName(fishDto.getName());
		fish.setPrice(fishDto.getPrice());
		fish.setCatchDate(new Date());

		String uploadDir = "public/images/";
		Path uploadPath = Paths.get(uploadDir);

		try {
			if (!Files.exists(uploadPath)) {
				Files.createDirectories(uploadPath);
			}

			for (MultipartFile image : fishDto.getImageFiles()) {
				if (!image.isEmpty()) {
					String storageFileName = new Date().getTime() + "_" + image.getOriginalFilename();
					try (InputStream inputStream = image.getInputStream()) {
						Files.copy(inputStream, Paths.get(uploadDir + storageFileName),
								StandardCopyOption.REPLACE_EXISTING);

						technikal.task.fishmarket.models.FishImage fishImage = new technikal.task.fishmarket.models.FishImage();
						fishImage.setFileName(storageFileName);
						fishImage.setFish(fish);
						fish.getImages().add(fishImage);
					}
				}
			}

		} catch (Exception ex) {
			System.out.println("Exception: " + ex.getMessage());
		}

		repo.save(fish);

		return "redirect:/fish";
	}

}
