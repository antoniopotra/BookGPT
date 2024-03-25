package software.design.gamegpt.service;

import software.design.gamegpt.model.Game;

import java.util.List;

public interface GameService {
    List<Game> getShowcaseGames();

    Game getGameByName(String name);

    Game getGameById(Long id);
}
