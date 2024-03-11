package software.design.gamegpt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import software.design.gamegpt.entity.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Role findByName(String name);
}
