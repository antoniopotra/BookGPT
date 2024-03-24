package software.design.gamegpt.service;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.annotation.PostConstruct;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import software.design.gamegpt.model.Game;
import software.design.gamegpt.model.Genre;
import software.design.gamegpt.utils.TimeMapper;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class GameServiceImpl implements GameService {
    private static final String CLIENT_ID = "e6kuql9svsnyqm9iwo7wr07kdm6cl3";
    private static final String CLIENT_SECRET = "u81l4fx55zjhmbzv580i119c2258bb";
    private static final String GRANT_TYPE = "client_credentials";
    private static final String BASE_URL = "https://api.igdb.com/v4/";
    private final HttpHeaders headers = new HttpHeaders();
    private String accessToken;

    @PostConstruct
    private void init() {
        generateAccessToken();
        generateHeaders();
    }

    @Override
    public List<Game> getGames() {
        List<IgdbGameResponse> igdbGames = getIgdbGames();
        return igdbGames == null ? null : igdbGames.stream().map(igdbGame ->
                new Game(igdbGame.id, igdbGame.name, igdbGame.summary, getCoverById(igdbGame.cover), igdbGame.url, getGenresByIds(igdbGame.genres), TimeMapper.getYearFromUnixTime(igdbGame.releaseDate))
        ).collect(Collectors.toList());
    }

    @Override
    public Game getGameByName(String name) {
        IgdbGameResponse igdbGame = getIgdbGameByName(name);
        return igdbGame == null ? null : new Game(igdbGame.id, igdbGame.name, igdbGame.summary, getCoverById(igdbGame.cover), igdbGame.url, getGenresByIds(igdbGame.genres), TimeMapper.getYearFromUnixTime(igdbGame.releaseDate));
    }

    private String getCoverById(Long id) {
        RestTemplate restTemplate = new RestTemplate();
        String body = String.format("fields url; where id = %d;", id);
        IgdbCoverResponse[] coverResponses = restTemplate.postForObject(BASE_URL + "covers", new HttpEntity<>(body, headers), IgdbCoverResponse[].class);
        return coverResponses == null ? null : "https:" + coverResponses[0].url.replace("t_thumb", "t_cover_big");
    }

    private List<Genre> getGenresByIds(List<Long> ids) {
        return ids.stream().map(this::getGenreById).filter(Objects::nonNull).collect(Collectors.toList());
    }

    private Genre getGenreById(Long id) {
        RestTemplate restTemplate = new RestTemplate();
        String body = String.format("fields name; where id = %d;", id);
        Genre[] genres = restTemplate.postForObject(BASE_URL + "genres", new HttpEntity<>(body, headers), Genre[].class);
        return genres == null ? null : genres[0];
    }

    private List<IgdbGameResponse> getIgdbGames() {
        String body = "fields id, name, summary, cover, url, genres, first_release_date; where name != null & summary != null & cover != null & url != null & genres != null & first_release_date != null & rating >= 80 & rating_count >= 200; limit 12;";
        RestTemplate restTemplate = new RestTemplate();
        IgdbGameResponse[] gameResponses = restTemplate.postForObject(BASE_URL + "games", new HttpEntity<>(body, headers), IgdbGameResponse[].class);
        return gameResponses == null ? null : List.of(gameResponses);
    }

    private IgdbGameResponse getIgdbGameByName(String name) {
        String body = String.format("fields id, name, summary, cover, url, genres, first_release_date; where name = \"%s\";", name);
        RestTemplate restTemplate = new RestTemplate();
        IgdbGameResponse[] gameResponses = restTemplate.postForObject(BASE_URL + "games", new HttpEntity<>(body, headers), IgdbGameResponse[].class);
        return gameResponses == null ? null : gameResponses[0];
    }

    private void generateHeaders() {
        headers.set("Client-ID", CLIENT_ID);
        headers.set("Authorization", "Bearer " + accessToken);
    }

    private void generateAccessToken() {
        RestTemplate restTemplate = new RestTemplate();
        IgdbAuthResponse authResponse = restTemplate.postForObject(buildAuthUrl(), null, IgdbAuthResponse.class);
        accessToken = (authResponse == null ? null : authResponse.accessToken);
    }

    private String buildAuthUrl() {
        return String.format("https://id.twitch.tv/oauth2/token?client_id=%s&client_secret=%s&grant_type=%s", CLIENT_ID, CLIENT_SECRET, GRANT_TYPE);
    }

    private record IgdbCoverResponse(String url) {
    }

    private record IgdbGameResponse(Long id, String name, String summary, Long cover, String url, List<Long> genres,
                                    @JsonProperty("first_release_date") Long releaseDate) {
    }

    private record IgdbAuthResponse(@JsonProperty("access_token") String accessToken) {
    }
}
