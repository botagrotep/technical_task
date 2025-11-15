package technikal.task.fishmarket.repasitories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import technikal.task.fishmarket.models.*;

public interface FishImageRepository extends JpaRepository<FishImage, Long >{
List<FishImage> findByFish(Fish fish);
}
