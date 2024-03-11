package software.design.gamegpt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import software.design.gamegpt.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);

    User findByEmail(String email);
}
