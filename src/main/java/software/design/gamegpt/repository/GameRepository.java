package software.design.gamegpt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import software.design.gamegpt.model.Game;

public interface GameRepository extends JpaRepository<Game, Long> {
}
