package technikal.task.fishmarket.controllers;


import java.io.IOException;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.Valid;
import technikal.task.fishmarket.models.Fish;
import technikal.task.fishmarket.models.FishDto;
import technikal.task.fishmarket.repositories.FishRepository;
import technikal.task.fishmarket.services.FishService;

@Controller
@RequestMapping("/fish")
public class FishController {

    private final FishService service;

    public FishController(FishService service) {
        this.service = service;
    }

    @GetMapping({"", "/"})
    public String showFishList(Model model) {
        List<Fish> fishlist = service.getAllFish();
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
        service.deleteFish(id);
        return "redirect:/fish";
    }

    @PostMapping("/create")
    public String addFish(@Valid @ModelAttribute FishDto fishDto, BindingResult result) {
        List<MultipartFile> files = fishDto.getImageFiles();
        if ((files == null) || files.stream().allMatch(MultipartFile::isEmpty)) {
            result.addError(new FieldError("fishDto", "imageFiles", "Потрібно завантажити хоча б одне фото"));
        }

        if (result.hasErrors()) {
            return "createFish";
        }

        try {
            service.saveFish(fishDto);
        } catch (IOException e) {
            System.out.println("Exception: " + e.getMessage());
            result.reject("imageFile", "Помилка при збереженні зображення");
            return "createFish";
        }

        return "redirect:/fish";
    }

}
