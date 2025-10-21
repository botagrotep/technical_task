package technikal.task.fishmarket.controllers;

import java.io.InputStream;
import java.nio.file.*;
import java.util.ArrayList;
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

import jakarta.validation.Valid;
import technikal.task.fishmarket.models.Fish;
import technikal.task.fishmarket.models.FishDto;
import technikal.task.fishmarket.models.FishImage;
import technikal.task.fishmarket.services.FishRepository;
import technikal.task.fishmarket.services.FishImageRepository;

@Controller
@RequestMapping("/fish")
public class FishController {

    @Autowired
    private FishRepository fishRepo;

    @Autowired
    private FishImageRepository imageRepo;

    @GetMapping({"", "/"})
    public String showFishList(Model model) {
        List<Fish> fishlist = fishRepo.findAll(Sort.by(Sort.Direction.DESC, "id"));
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
            Fish fish = fishRepo.findById(id).orElse(null);
            if (fish == null) return "redirect:/fish";

            // удалить все изображения с диска
            if (fish.getImages() != null) {
                for (FishImage img : fish.getImages()) {
                    Path imagePath = Paths.get("public/images/" + img.getImageFileName());
                    Files.deleteIfExists(imagePath);
                }
            }

            fishRepo.delete(fish);
        } catch (Exception ex) {
            System.out.println("Exception: " + ex.getMessage());
        }
        return "redirect:/fish";
    }

    @PostMapping("/create")
    public String addFish(@Valid @ModelAttribute FishDto fishDto, BindingResult result) {

        if (fishDto.getImageFiles() == null || fishDto.getImageFiles().isEmpty()) {
            result.addError(new FieldError("fishDto", "imageFiles", "Потрібне хоча б одне фото рибки"));
        }

        if (result.hasErrors()) {
            return "createFish";
        }

        Date catchDate = new Date();
        Fish fish = new Fish();
        fish.setName(fishDto.getName());
        fish.setPrice(fishDto.getPrice());
        fish.setCatchDate(catchDate);

        fishRepo.save(fish);

        String uploadDir = "public/images/";
        Path uploadPath = Paths.get(uploadDir);
        try {
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            List<FishImage> savedImages = new ArrayList<>();

            for (MultipartFile image : fishDto.getImageFiles()) {
                if (image.isEmpty()) continue;

                String storageFileName = catchDate.getTime() + "_" + image.getOriginalFilename();
                try (InputStream inputStream = image.getInputStream()) {
                    Files.copy(inputStream, uploadPath.resolve(storageFileName), StandardCopyOption.REPLACE_EXISTING);
                }

                FishImage fishImage = new FishImage();
                fishImage.setFish(fish);
                fishImage.setImageFileName(storageFileName);
                savedImages.add(fishImage);
            }

            imageRepo.saveAll(savedImages);

        } catch (Exception ex) {
            System.out.println("Exception: " + ex.getMessage());
        }

        return "redirect:/fish";
    }
}