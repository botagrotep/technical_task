package technikal.task.fishmarket.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import technikal.task.fishmarket.dto.FishDto;
import technikal.task.fishmarket.models.Fish;
import technikal.task.fishmarket.repository.FishRepository;
import technikal.task.fishmarket.services.FishService;
import technikal.task.fishmarket.services.ImageService;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FishServiceImpl implements FishService {

    private final ImageService imageService;

    private final FishRepository fishRepository;

    @Override
    public List<Fish> findAll() {
        return fishRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));
    }

    @Override
    public void create(FishDto fishDto) {

        var images = fishDto.getImageFiles();
        var imageNames = imageService.save(images);

        var fish = new Fish();
        fish.setCatchDate(new Date());
        var catchDate = new Date();
        fish.setImageFileNames(imageNames);
        fish.setName(fishDto.getName());
        fish.setPrice(fishDto.getPrice());

        fishRepository.save(fish);
    }

    @Override
    public void deleteById(int id) {
        try {
            var fish = fishRepository.findById(id).get();
            var imagePaths = fish.getImageFileNames().stream().map(it -> Paths.get("public/images/"+ it)).toList();
            imagePaths.forEach(it -> {
                try {
                    if (Files.exists(it)) {
                        Files.delete(it);
                    } else {
                        System.out.println("Warning: Image file not found: " + it);
                    }
                } catch (IOException e) {
                    System.out.println("Warning: Failed to delete image file: " + it + ", error: " + e.getMessage());
                }
            });
            fishRepository.delete(fish);
        }
        catch(Exception ex) {
            System.out.println("Exception: " + ex.getMessage());
        }
    }
}
