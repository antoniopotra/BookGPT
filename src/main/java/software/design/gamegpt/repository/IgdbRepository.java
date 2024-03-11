package software.design.gamegpt.repository;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestTemplate;
import software.design.gamegpt.model.IgdbAuthResponse;

public class IgdbRepository {
    private static final String CLIENT_ID = "e6kuql9svsnyqm9iwo7wr07kdm6cl3";
    private static final String CLIENT_SECRET = "u81l4fx55zjhmbzv580i119c2258bb";
    private static final String GRANT_TYPE = "client_credentials";
    private static final String URL = "https://api.igdb.com/v4/genres";

    public String getGames() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Client-ID", CLIENT_ID);
        headers.set("Authorization", "Bearer " + generateAccessToken());

        String body = "fields *; where name = \"Indie\";";
        HttpEntity<String> entity = new HttpEntity<>(body, headers);

        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.postForObject(URL, entity, String.class);
    }

    private String generateAccessToken() {
        RestTemplate restTemplate = new RestTemplate();
        IgdbAuthResponse authResponse = restTemplate.postForObject(buildAuthUrl(), null, IgdbAuthResponse.class);
        return authResponse != null ? authResponse.accessToken() : null;
    }

    private String buildAuthUrl() {
        return String.format("https://id.twitch.tv/oauth2/token?client_id=%s&client_secret=%s&grant_type=%s", CLIENT_ID, CLIENT_SECRET, GRANT_TYPE);
    }
}
