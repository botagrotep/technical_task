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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.Valid;
import technikal.task.fishmarket.dto.FishDto;
import technikal.task.fishmarket.dto.FishUpdateDto;
import technikal.task.fishmarket.models.Fish;
import technikal.task.fishmarket.models.FishImage;
import technikal.task.fishmarket.services.FishImageService;
import technikal.task.fishmarket.services.FishRepository;

@Controller
@RequestMapping("/fish")
public class FishController {
	
	@Autowired
	private FishRepository repo;
	
	@Autowired
	private FishImageService fishImageService;
	
	@GetMapping({"", "/"})
	public String showFishList(Model model) {
		List<Fish> fishlist = repo.findAll(Sort.by(Sort.Direction.DESC, "id"));
		model.addAttribute("fishlist", fishlist);
		return "index";
	}
	
	@PreAuthorize("hasRole('ADMIN')")
	@GetMapping("/create")
	public String showCreatePage(Model model) {
		FishDto fishDto = new FishDto();
		model.addAttribute("fishDto", fishDto);
		return "createFish";
	}
	
	@PreAuthorize("hasRole('ADMIN')")
	@GetMapping("/delete")
	public String deleteFish(@RequestParam int id) {
		
		try {
			
			Fish fish = repo.findById(id).get();
			
			Path imagePath = Paths.get("public/images/"+fish.getImageFileName());
			Files.delete(imagePath);
			repo.delete(fish);
			
		}catch(Exception ex) {
			System.out.println("Exception: " + ex.getMessage());
		}
		
		return "redirect:/fish";
	}
	
	@PreAuthorize("hasRole('ADMIN')")
	@PostMapping("/create")
	public String addFish(@Valid @ModelAttribute FishDto fishDto, BindingResult result) {
		
		if(fishDto.getImageFile().isEmpty()) {
			result.addError(new FieldError("fishDto", "imageFile", "Потрібне фото рибки"));
		}
		
		if(result.hasErrors()) {
			return "createFish";
		}
		 
		MultipartFile image = fishDto.getImageFile();
		Date catchDate = new Date();
		String  storageFileName = catchDate.getTime() + "_" + image.getOriginalFilename();
		
		try {
			String uploadDir = "public/images/";
			Path uploadPath = Paths.get(uploadDir);
			
			if(!Files.exists(uploadPath)) {
				Files.createDirectories(uploadPath);
			}
			 
			try(InputStream inputStream = image.getInputStream()){
				Files.copy(inputStream, Paths.get(uploadDir+storageFileName), StandardCopyOption.REPLACE_EXISTING);
			}
			
		}catch(Exception ex) {
			System.out.println("Exception: " + ex.getMessage());
		}
		
		Fish fish = new Fish();
		
		fish.setCatchDate(catchDate);
		fish.setImageFileName(storageFileName);
		fish.setName(fishDto.getName());
		fish.setPrice(fishDto.getPrice());
		
		repo.save(fish);
		
		return "redirect:/fish";
	}
	
	@PreAuthorize("hasRole('ADMIN')")
	@GetMapping("/edit/{id}")
	public String editFishForm(@PathVariable int id, Model model) {
	    Fish fish = repo.findById(id)
	            .orElseThrow(() -> new RuntimeException("Fish not found"));
	    List<FishImage> images = fishImageService.getImagesFish(fish);

	    if(fish.getImageFileName() != null && !fish.getImageFileName().isEmpty()) {
	        FishImage mainImage = new FishImage();
	        mainImage.setFileName(fish.getImageFileName());
	        images.add(0, mainImage); 
	    }

	    FishUpdateDto dto = new FishUpdateDto();
	    dto.setId(fish.getId());
	    dto.setName(fish.getName());
	    dto.setPrice(fish.getPrice());

	    model.addAttribute("fishUpdateDto", dto);
	    model.addAttribute("fish", fish);
	    model.addAttribute("images", images);

	    return "editFish"; 
	}


	@PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/edit")
    public String updateFish(@ModelAttribute FishUpdateDto dto) {
        fishImageService.updateFish(dto);
        return "redirect:/fish";
    }

}
