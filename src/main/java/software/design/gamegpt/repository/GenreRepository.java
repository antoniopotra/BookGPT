package software.design.gamegpt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import software.design.gamegpt.model.Genre;

@Repository
public interface GenreRepository extends JpaRepository<Genre, Long> {
}
