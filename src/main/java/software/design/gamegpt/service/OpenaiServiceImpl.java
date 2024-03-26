package software.design.gamegpt.service;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.annotation.PostConstruct;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import software.design.gamegpt.model.Game;
import software.design.gamegpt.model.User;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OpenaiServiceImpl implements OpenaiService {
    private final HttpHeaders headers = new HttpHeaders();
    private final List<String> recommendations = new ArrayList<>();

    @PostConstruct
    private void init() {
        generateHeaders();
    }

    @Override
    public List<String> getRecommendations(User user) {
        if (!recommendations.isEmpty()) {
            System.out.println(recommendations);
            return recommendations;
        }

        RestTemplate restTemplate = new RestTemplate();
        OpenaiRequest body = new OpenaiRequest(buildPrompt(user));
        OpenaiResponse response = restTemplate.postForObject("https://api.openai.com/v1/chat/completions", new HttpEntity<>(body, headers), OpenaiResponse.class);
        if (response == null || response.choices.isEmpty()) {
            return List.of("There Is No Game");
        }

        String[] gameNames = response.choices.getFirst().message.content.split(",");
        recommendations.addAll(Arrays.stream(gameNames).map(String::trim).toList());
        return recommendations;
    }

    private String buildPrompt(User user) {
        List<Game> playedNotLiked = new ArrayList<>();
        for (Game game : user.getPlayedGames()) {
            if (!user.hasLikedGame(game)) {
                playedNotLiked.add(game);
            }
        }
        String likedGamesString = user.getLikedGames().stream().map(Game::getName).collect(Collectors.joining(", "));
        String playedNotLikedString = playedNotLiked.stream().map(Game::getName).collect(Collectors.joining(", "));
        return String.format("I like %s. Give me 6 recommendations, names only (no numbers), comma-separated list.", likedGamesString);
    }

    private void generateHeaders() {
        headers.set("Content-Type", "application/json");
        headers.set("Authorization", "Bearer YOUR_KEY");
    }

    private record OpenaiMessage(@JsonProperty("role") String role, @JsonProperty("content") String content) {
    }

    private record OpenaiChoice(@JsonProperty("message") OpenaiMessage message) {
    }

    private record OpenaiResponse(@JsonProperty("choices") List<OpenaiChoice> choices) {
    }

    private static class OpenaiRequest {
        @JsonProperty("model")
        private final String model = "gpt-3.5-turbo";

        @JsonProperty("temperature")
        private final double temperature = 0.7;

        @JsonProperty("messages")
        private final List<OpenaiMessage> messages = new ArrayList<>();

        public OpenaiRequest(String prompt) {
            messages.add(new OpenaiMessage("user", prompt));
        }
    }
}
