package technikal.task.fishmarket.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

@Data
public class FishDto {

	@NotEmpty(message = "потрібна назва рибки")
	private String name;

	@Min(0)
	private double price;

	private List<MultipartFile> imageFiles;

}
