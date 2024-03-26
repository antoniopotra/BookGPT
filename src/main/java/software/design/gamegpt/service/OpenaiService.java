package software.design.gamegpt.service;

import software.design.gamegpt.model.User;

import java.util.List;

public interface OpenaiService {
    List<String> getRecommendations(User user);
}
