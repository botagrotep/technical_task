package technikal.task.fishmarket.dtos.fish;

import java.util.List;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;

public class FishDto {
	

	@NotEmpty(message = "потрібна назва рибки")
	private String name;
	@Min(0)
	private double price;
	private List<MultipartFile> imageFileNames;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public double getPrice() {
		return price;
	}
	public void setPrice(double price) {
		this.price = price;
	}
	public List<MultipartFile> getImageFileNames() {
		return imageFileNames;
	}
	public void setImageFileNames(List<MultipartFile> imageFileNames) {
		this.imageFileNames = imageFileNames;
	}

}
