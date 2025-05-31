package technikal.task.fishmarket.controllers;


import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Date;
import java.util.List;

import lombok.RequiredArgsConstructor;
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
import technikal.task.fishmarket.dto.FishDto;
import technikal.task.fishmarket.repository.FishRepository;
import technikal.task.fishmarket.services.FishService;

@Controller
@RequestMapping("/fish")
@RequiredArgsConstructor
public class FishController {

	private final FishService fishService;

	@GetMapping({"", "/"})
	public String showFishList(Model model) {
		var fishlist = fishService.findAll();
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

		fishService.deleteById(id);

		return "redirect:/fish";
	}

	@PostMapping("/create")
	public String addFish(@Valid @ModelAttribute FishDto fishDto, BindingResult result) {

		if(fishDto.getImageFiles() == null || fishDto.getImageFiles().isEmpty() || fishDto.getImageFiles().stream().anyMatch(MultipartFile::isEmpty)) {
			result.addError(new FieldError("fishDto", "imageFiles", "Потрібні фото рибки"));
		}

		if(result.hasErrors()) {
			return "createFish";
		}

		fishService.create(fishDto);

		return "redirect:/fish";
	}

}
