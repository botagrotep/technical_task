package technikal.task.fishmarket.models;


import jakarta.persistence.*;

@Entity
@Table(name = "fish_image")
public class FishImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name="image_file_name")
    private String image_file_name;
    @ManyToOne
    @JoinColumn(name = "fish_id")
    private Fish fish;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFileName() {
        return image_file_name;
    }

    public void setFileName(String fileName) {
        this.image_file_name = fileName;
    }

    public Fish getFish() {
        return fish;
    }

    public void setFish(Fish fish) {
        this.fish = fish;
    }
}
