package software.design.gamegpt.service;

import software.design.gamegpt.model.Game;

import java.util.List;

public interface GameService {
    List<Game> getGames();

    Game getGameByName(String name);
}
