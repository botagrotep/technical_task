package technikal.task.fishmarket.dto;

import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public class FishUpdateDto {

	 private int id; 
	    private String name;
	    private double price;
	    private List<MultipartFile> images;
	    private List<Long> imagesToDelete;

	    public int getId() { return id; }
	    public void setId(int id) { this.id = id; }

	    public String getName() { return name; }
	    public void setName(String name) { this.name = name; }

	    public double getPrice() { return price; }
	    public void setPrice(double price) { this.price = price; }

	    public List<MultipartFile> getImages() { return images; }
	    public void setImages(List<MultipartFile> images) { this.images = images; }
	    
	    public List<Long> getImagesToDelete() { return imagesToDelete; }
	    public void setImagesToDelete(List<Long> imagesToDelete) { this.imagesToDelete = imagesToDelete; }
}
