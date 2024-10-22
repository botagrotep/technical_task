package technikal.task.fishmarket.controllers;


import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
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
import technikal.task.fishmarket.dtos.fish.FishDto;
import technikal.task.fishmarket.models.Fish;
import technikal.task.fishmarket.repositories.FishRepository;

@Controller
@RequestMapping("/fish")
public class FishController {
	
	@Autowired
	private FishRepository repo;
	@GetMapping({"", "/"})
	public String showFishList(Model model) {
		List<Fish> fishlist = repo.findAll(Sort.by(Sort.Direction.DESC, "id"));
		model.addAttribute("fishlist", fishlist);
		return "index";
	}

	@GetMapping({ "/user"})
	public String showFishListFromUser(Model model) {
		List<Fish> fishlist = repo.findAll(Sort.by(Sort.Direction.DESC, "id"));
		model.addAttribute("fishlist", fishlist);
		return "userindex";
	}

	@GetMapping("/create")
	public String showCreatePage(Model model) {
		FishDto fishDto = new FishDto();
		model.addAttribute("fishDto", fishDto);
		return "createFish";
	}
	
	@GetMapping("/delete")
	public String deleteFish(@RequestParam int id) {

			Fish fish = repo.findById(id).orElseThrow(
					() -> new EntityNotFoundException("Can't find fish by id: " + id)
			);

			Set<String> imageFileNames = fish.getImageFileNames();
			imageFileNames.stream()
					.map(imageFileName -> "public/images/" + imageFileName)
					.map(Paths::get)
							.forEach(imagePath -> {
								try {
									Files.delete(imagePath);
								} catch (IOException e) {
									throw new RuntimeException("Can't delete file: " + imagePath);
								}
							});

			repo.delete(fish);
		
		return "redirect:/fish";
	}
	
	@PostMapping("/create")
	public String addFish(@Valid @ModelAttribute FishDto fishDto, BindingResult result) {
		
		if(fishDto.getImageFileNames().isEmpty()) {
			result.addError(new FieldError(
					"fishDto",
					"imageFiles",
					"Потрібне принаймні одне фото рибки"
			));
		}
		
		if(result.hasErrors()) {
			return "createFish";
		}
		Set<String> storageFileNames = new HashSet<>();
		List<MultipartFile> images = fishDto.getImageFileNames();
		Date catchDate = new Date();
		try {
			String uploadDir = "public/images/";
			Path uploadPath = Paths.get(uploadDir);
			
			if(!Files.exists(uploadPath)) {
				Files.createDirectories(uploadPath);
			}

			for (MultipartFile image : images) {
				String  storageFileName = catchDate.getTime() + "_" + image.getOriginalFilename();
				try(InputStream inputStream = image.getInputStream()){
					Files.copy(
							inputStream,
							Paths.get(uploadDir+storageFileName),
							StandardCopyOption.REPLACE_EXISTING
					);
				}
				storageFileNames.add(storageFileName);
			}
			
		}catch(Exception ex) {
			System.out.println("Exception: " + ex.getMessage());
		}
		
		Fish fish = new Fish();
		
		fish.setCatchDate(catchDate);
		fish.setImageFileNames(storageFileNames);
		fish.setName(fishDto.getName());
		fish.setPrice(fishDto.getPrice());
		
		repo.save(fish);
		
		return "redirect:/fish";
	}

}
