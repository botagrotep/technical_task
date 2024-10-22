package technikal.task.fishmarket.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import technikal.task.fishmarket.models.Fish;

public interface FishRepository extends JpaRepository<Fish, Integer> {

}
