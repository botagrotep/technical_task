package technikal.task.fishmarket.models;

import java.util.Date;
import java.util.Set;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;

@Entity
@Table(name = "fish")
public class Fish {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int id;
	
	private String name;
	private double price;
	private Date catchDate;

	@ElementCollection
	@CollectionTable(name = "fish_images", joinColumns = @JoinColumn(name = "fish_id"))
	@Column(name = "image_file_name")
	private Set<String> imageFileNames;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
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
	public Date getCatchDate() {
		return catchDate;
	}
	public void setCatchDate(Date catchDate) {
		this.catchDate = catchDate;
	}
	public Set<String> getImageFileNames() {
		return imageFileNames;
	}
	public void setImageFileNames(Set<String> imageFileNames) {
		this.imageFileNames = imageFileNames;
	}

}
