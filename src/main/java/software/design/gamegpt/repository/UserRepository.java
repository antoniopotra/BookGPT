package software.design.gamegpt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import software.design.gamegpt.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);

    User findByEmail(String email);

    @Modifying
    @Transactional
    @Query(value = "insert into users_games_played (user_id, game_id) values (?1, ?2)", nativeQuery = true)
    void addPlayedGame(Long userId, Long gameId);

    @Modifying
    @Transactional
    @Query(value = "insert into users_games_liked (user_id, game_id) values (?1, ?2)", nativeQuery = true)
    void addLikedGame(Long userId, Long gameId);

    @Modifying
    @Transactional
    @Query(value = "delete from users_games_played where user_id = ?1 and game_id = ?2", nativeQuery = true)
    void removePlayedGame(Long userId, Long gameId);

    @Modifying
    @Transactional
    @Query(value = "delete from users_games_liked where user_id = ?1 and game_id = ?2", nativeQuery = true)
    void removeLikedGame(Long userId, Long gameId);
}
