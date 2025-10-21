package technikal.task.fishmarket.models;

import jakarta.persistence.*;

@Entity
@Table(name = "fish_images")
public class FishImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "fish_id", nullable = false)
    private Fish fish;

    @Column(name = "image_file_name", nullable = false)
    private String imageFileName;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Fish getFish() {
        return fish;
    }

    public void setFish(Fish fish) {
        this.fish = fish;
    }

    public String getImageFileName() {
        return imageFileName;
    }

    public void setImageFileName(String imageFileName) {
        this.imageFileName = imageFileName;
    }
}