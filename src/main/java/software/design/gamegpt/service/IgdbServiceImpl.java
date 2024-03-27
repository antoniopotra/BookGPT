package software.design.gamegpt.service;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import software.design.gamegpt.model.Game;
import software.design.gamegpt.model.Genre;
import software.design.gamegpt.repository.GameRepository;
import software.design.gamegpt.utils.CategoryMapper;
import software.design.gamegpt.utils.TimeMapper;

import java.util.*;

@Service
public class IgdbServiceImpl implements IgdbService {
    private static final String BASE_URL = "https://api.igdb.com/v4/";
    private final GameRepository gameRepository;
    private final HttpHeaders headers = new HttpHeaders();
    private final List<Game> showcaseGames = new ArrayList<>();
    private final Map<Long, Game> gameTable = new HashMap<>();
    @Value("${igdb.client.id}")
    private String clientId;
    @Value("${igdb.client.secret}")
    private String clientSecret;
    private String accessToken;
    private Game defaultGame = null;

    public IgdbServiceImpl(GameRepository gameRepository) {
        this.gameRepository = gameRepository;
    }

    @PostConstruct
    private void init() {
        generateAccessToken();
        generateHeaders();
        fetchDefaultGame();
    }

    @Override
    public List<Game> getShowcaseGames() {
        if (!showcaseGames.isEmpty()) {
            return showcaseGames;
        }

        RestTemplate restTemplate = new RestTemplate();
        String body = "fields *; where category = 0 & rating >= 80 & rating_count >= 200; limit 12;";
        IgdbGameResponse[] igdbGames = restTemplate.postForObject(BASE_URL + "games", new HttpEntity<>(body, headers), IgdbGameResponse[].class);
        if (igdbGames == null || igdbGames.length == 0) {
            return List.of(defaultGame);
        }

        for (IgdbGameResponse igdbGame : igdbGames) {
            Game game = igdbGameToGame(igdbGame);
            showcaseGames.add(game);
            gameTable.put(game.getId(), game);
        }
        return showcaseGames;
    }

    @Override
    public List<Game> getGamesByName(String name) {
        List<Game> games = new ArrayList<>();
        for (Game game : gameTable.values()) {
            if (game.getName().equals(name)) {
                games.add(game);
            }
        }
        if (!games.isEmpty()) {
            return games;
        }

        RestTemplate restTemplate = new RestTemplate();
        String body = String.format("fields *; where name = \"%s\";", name);
        IgdbGameResponse[] igdbGames = restTemplate.postForObject(BASE_URL + "games", new HttpEntity<>(body, headers), IgdbGameResponse[].class);
        if (igdbGames == null || igdbGames.length == 0) {
            return List.of(defaultGame);
        }

        games.addAll(Arrays.stream(igdbGames).map(this::igdbGameToGame).toList());
        for (Game game : games) {
            gameTable.put(game.getId(), game);
        }
        return games;
    }

    @Override
    public Game getGameById(Long id) {
        Game game = gameTable.get(id);
        if (game != null) {
            return game;
        }

        Optional<Game> gameOpt = gameRepository.findById(id);
        if (gameOpt.isPresent()) {
            return gameOpt.get();
        }

        RestTemplate restTemplate = new RestTemplate();
        String body = String.format("fields *; where id = %d;", id);
        IgdbGameResponse[] igdbGames = restTemplate.postForObject(BASE_URL + "games", new HttpEntity<>(body, headers), IgdbGameResponse[].class);
        if (igdbGames == null || igdbGames.length == 0) {
            return defaultGame;
        }

        game = igdbGameToGame(igdbGames[0]);
        gameTable.put(game.getId(), game);
        return game;
    }

    private void fetchDefaultGame() {
        RestTemplate restTemplate = new RestTemplate();
        String body = "fields *; where name = \"There Is No Game\";";
        IgdbGameResponse[] igdbGames = restTemplate.postForObject(BASE_URL + "games", new HttpEntity<>(body, headers), IgdbGameResponse[].class);
        if (igdbGames != null && igdbGames.length > 0) {
            defaultGame = igdbGameToGame(igdbGames[0]);
        }
    }

    private Game igdbGameToGame(IgdbGameResponse igdbGame) {
        String cover = getCoverById(igdbGame.cover);
        List<Genre> genres = getGenresByIds(igdbGame.genres);
        int year = TimeMapper.getYearFromUnixTime(igdbGame.releaseDate);
        String category = CategoryMapper.toCategoryString(igdbGame.category);
        return new Game(igdbGame.id, igdbGame.name, igdbGame.summary, cover, igdbGame.url, genres, year, category);
    }

    private String getCoverById(Long id) {
        RestTemplate restTemplate = new RestTemplate();
        String body = String.format("fields *; where id = %d;", id);
        IgdbCoverResponse[] covers = restTemplate.postForObject(BASE_URL + "covers", new HttpEntity<>(body, headers), IgdbCoverResponse[].class);
        return covers != null && covers.length > 0 ? "https:" + covers[0].url.replace("t_thumb", "t_cover_big") : null;
    }

    private List<Genre> getGenresByIds(List<Long> ids) {
        return ids.stream().map(this::getGenreById).filter(Objects::nonNull).toList();
    }

    private Genre getGenreById(Long id) {
        RestTemplate restTemplate = new RestTemplate();
        String body = String.format("fields *; where id = %d;", id);
        Genre[] genres = restTemplate.postForObject(BASE_URL + "genres", new HttpEntity<>(body, headers), Genre[].class);
        return genres != null && genres.length > 0 ? genres[0] : null;
    }

    private String buildAuthUrl() {
        return String.format("https://id.twitch.tv/oauth2/token?client_id=%s&client_secret=%s&grant_type=%s", clientId, clientSecret, "client_credentials");
    }

    private void generateAccessToken() {
        RestTemplate restTemplate = new RestTemplate();
        IgdbAuthResponse authResponse = restTemplate.postForObject(buildAuthUrl(), null, IgdbAuthResponse.class);
        accessToken = (authResponse != null ? authResponse.accessToken : null);
    }

    private void generateHeaders() {
        headers.set("Client-ID", clientId);
        headers.set("Authorization", "Bearer " + accessToken);
    }

    private record IgdbCoverResponse(@JsonProperty("url") String url) {
    }

    private record IgdbGameResponse(@JsonProperty("id") Long id, @JsonProperty("name") String name,
                                    @JsonProperty("summary") String summary, @JsonProperty("cover") Long cover,
                                    @JsonProperty("url") String url, @JsonProperty("genres") List<Long> genres,
                                    @JsonProperty("first_release_date") Long releaseDate,
                                    @JsonProperty("category") String category) {
    }

    private record IgdbAuthResponse(@JsonProperty("access_token") String accessToken) {
    }
}
