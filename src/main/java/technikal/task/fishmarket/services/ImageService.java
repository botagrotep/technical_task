package technikal.task.fishmarket.services;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ImageService {

    List<String> save(List<MultipartFile> images);
}
