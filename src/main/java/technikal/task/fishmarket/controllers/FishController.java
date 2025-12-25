package technikal.task.fishmarket.controllers;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.validation.Valid;
import technikal.task.fishmarket.models.Fish;
import technikal.task.fishmarket.models.FishDto;
import technikal.task.fishmarket.models.FishImage;
import technikal.task.fishmarket.services.FishRepository;

@Controller
@RequestMapping("/fish")
public class FishController {

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
		FishDto fishDto = new FishDto();
		model.addAttribute("fishDto", fishDto);
		return "createFish";
	}

	@PostMapping("/create")
	public String addFish(@Valid @ModelAttribute FishDto fishDto, BindingResult result) {


		if (fishDto.getImageFiles() == null || fishDto.getImageFiles().get(0).isEmpty()) {
			result.addError(new FieldError("fishDto", "imageFiles", "Потрібне фото рибки"));
		}
		if (result.hasErrors()) {
			return "createFish";
		}

		Fish fish = new Fish();
		fish.setName(fishDto.getName());
		fish.setPrice(fishDto.getPrice());
		fish.setCatchDate(new Date());

		String uploadDir = "D:/technical_task/images/";
		Path uploadPath = Paths.get(uploadDir);

		try {

			if (!Files.exists(uploadPath)) {
				Files.createDirectories(uploadPath);
			}
			for (MultipartFile image : fishDto.getImageFiles()) {
				String storageFileName = System.currentTimeMillis() + "_" + image.getOriginalFilename();
				try (InputStream inputStream = image.getInputStream()) {
					Files.copy(inputStream, uploadPath.resolve(storageFileName), StandardCopyOption.REPLACE_EXISTING);
				}
				FishImage fishImage = new FishImage();
				fishImage.setFileName(storageFileName);
				fishImage.setFish(fish);
				fish.getImages().add(fishImage);

				repo.save(fish);

			}
		} catch (Exception ex) {
			System.out.println("Exception: " + ex.getMessage());
		}


		return "redirect:/fish";
	}

	@GetMapping("/delete")
	public String deleteFish(@RequestParam int id) {
		try {
			Fish fish = repo.findById(id).orElseThrow();
			String uploadDir = "D:/technical_task/images/";
			for (FishImage img : fish.getImages()) {
				Path imagePath = Paths.get(uploadDir + img.getFileName());
				Files.deleteIfExists(imagePath);
			}
			repo.delete(fish);
		} catch (Exception ex) {
			System.out.println("Exception: " + ex.getMessage());
		}
		return "redirect:/fish";
	}
}

