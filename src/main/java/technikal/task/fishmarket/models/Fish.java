package technikal.task.fishmarket.models;

import java.util.Date;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "fish")
public class Fish {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	private String name;
	private double price;
	private Date catchDate;
	@OneToMany(mappedBy = "fish", cascade = CascadeType.ALL, orphanRemoval = true)
	private java.util.List<FishImage> images = new java.util.ArrayList<>();

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

	public java.util.List<FishImage> getImages() {
		return images;
	}

	public void setImages(java.util.List<FishImage> images) {
		this.images = images;
	}

}
