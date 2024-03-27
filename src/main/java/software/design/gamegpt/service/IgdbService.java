package software.design.gamegpt.service;

import software.design.gamegpt.model.Game;

import java.util.List;

public interface IgdbService {
    List<Game> getShowcaseGames();

    List<Game> getGamesByName(String name);

    Game getGameById(Long id);
}
