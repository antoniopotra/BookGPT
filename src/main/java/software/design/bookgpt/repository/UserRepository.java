package software.design.bookgpt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import software.design.bookgpt.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);

    User findByEmail(String email);
}
