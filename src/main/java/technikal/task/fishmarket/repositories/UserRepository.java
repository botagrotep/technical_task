package technikal.task.fishmarket.repositories;

import java.util.Optional;
import technikal.task.fishmarket.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
}
