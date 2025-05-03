package technikal.task.fishmarket.services;

import org.springframework.data.jpa.repository.JpaRepository;
import technikal.task.fishmarket.models.Users;

import java.util.Optional;

public interface UsersRepository extends JpaRepository<Users, Integer> {

    Users findByLogin(String login);
}
