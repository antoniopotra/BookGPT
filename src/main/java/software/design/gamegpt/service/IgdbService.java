package software.design.gamegpt.service;

import software.design.gamegpt.model.Game;

import java.util.List;

public interface IgdbService {
    /**
     * Searches the IGDB Api for 12 games with a high number of positive user reviews
     *
     * @return a list of 12 games to be displayed on the main page
     */
    List<Game> getShowcaseGames();

    /**
     * Searches the IGDB Api for a particular game
     *
     * @param name the name of the game
     * @return a list of games, in case more games have the same name
     */
    List<Game> getGamesByName(String name);

    /**
     * Retrieves a game by id either from the database or from the api
     *
     * @param id the id of the game
     * @return a unique game
     */
    Game getGameById(Long id);
}
