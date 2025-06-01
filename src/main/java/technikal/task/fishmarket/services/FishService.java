package technikal.task.fishmarket.services;

import technikal.task.fishmarket.dto.FishDto;
import technikal.task.fishmarket.models.Fish;

import java.util.List;

public interface FishService {

    List<Fish> findAll();

    void create(FishDto fishDto);

    void deleteById(int id);
}
