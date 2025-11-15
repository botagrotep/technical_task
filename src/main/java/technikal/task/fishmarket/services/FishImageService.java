package technikal.task.fishmarket.services;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import technikal.task.fishmarket.dto.FishUpdateDto;
import technikal.task.fishmarket.models.Fish;
import technikal.task.fishmarket.models.FishImage;

public interface FishImageService {

	void saveFishImage(Fish fish, List<MultipartFile> files);
	
	List<FishImage> getImagesFish(Fish fish);
	
	void updateFish(FishUpdateDto dto);
	
	void deleteFishImage(Long imageId);
}
