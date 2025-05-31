package technikal.task.fishmarket.services.impl;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import technikal.task.fishmarket.services.ImageService;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Date;
import java.util.List;

@Service
public class ImageServiceImpl implements ImageService {

    @Override
    public List<String> save(List<MultipartFile> images) {
        var catchDate = new Date();
        return images.stream().map(image -> save(image, catchDate)).toList();
    }

    private String save(MultipartFile image, Date catchDate) {
        var storageFileName = catchDate.getTime() + "_" + image.getOriginalFilename();

        try {
            var uploadDir = "public/images/";
            var uploadPath = Paths.get(uploadDir);

            if(!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            try(InputStream inputStream = image.getInputStream()){
                Files.copy(inputStream, Paths.get(uploadDir+storageFileName), StandardCopyOption.REPLACE_EXISTING);
            }

            return storageFileName;

        }catch(Exception ex) {
            System.out.println("Exception: " + ex.getMessage());
            throw new RuntimeException(ex);
        }
    }
}
