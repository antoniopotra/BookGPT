package software.design.bookgpt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import software.design.bookgpt.entity.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Role findByName(String name);
}
