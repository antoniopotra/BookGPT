package software.design.gamegpt.service;

import software.design.gamegpt.model.Game;
import software.design.gamegpt.model.User;

import java.util.List;

public interface UserService {
    void save(User user);

    User findByUsername(String username);

    User findByEmail(String email);

    List<User> findAll();

    void addPlayedGame(User user, Game game);

    void addLikedGame(User user, Game game);

    void removePlayedGame(User user, Game game);

    void removeLikedGame(User user, Game game);
}
