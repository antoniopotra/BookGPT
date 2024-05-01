package software.design.gamegpt.service;

import software.design.gamegpt.model.User;

import java.util.List;

public interface OpenaiService {
    /**
     * Generates a list of recommendations for a user, based on the played and liked games
     *
     * @param user the user for which the recommendations should be made
     * @return a list of game names (strings)
     */
    List<String> getRecommendations(User user);
}
