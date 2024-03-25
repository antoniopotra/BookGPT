package software.design.gamegpt.service;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.annotation.PostConstruct;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import software.design.gamegpt.model.Game;
import software.design.gamegpt.model.Genre;
import software.design.gamegpt.repository.GameRepository;
import software.design.gamegpt.repository.GenreRepository;
import software.design.gamegpt.utils.TimeMapper;

import java.util.*;

@Service
public class GameServiceImpl implements GameService {
    private static final String CLIENT_ID = "e6kuql9svsnyqm9iwo7wr07kdm6cl3";
    private static final String CLIENT_SECRET = "u81l4fx55zjhmbzv580i119c2258bb";
    private static final String GRANT_TYPE = "client_credentials";
    private static final String BASE_URL = "https://api.igdb.com/v4/";
    private final GameRepository gameRepository;
    private final GenreRepository genreRepository;
    private final HttpHeaders headers = new HttpHeaders();
    private final List<Game> showcaseGames = new ArrayList<>();
    private String accessToken;
    private Game defaultGame = null;

    public GameServiceImpl(GameRepository gameRepository, GenreRepository genreRepository) {
        this.gameRepository = gameRepository;
        this.genreRepository = genreRepository;
    }

    @PostConstruct
    private void init() {
        generateAccessToken();
        generateHeaders();
        fetchDefaultGame();
    }

    @Override
    public List<Game> getShowcaseGames() {
        if (showcaseGames.isEmpty()) {
            String body = "fields id, name, summary, cover, url, genres, first_release_date; where name != null & summary != null & cover != null & url != null & genres != null & first_release_date != null & rating >= 80 & rating_count >= 200; limit 12;";
            RestTemplate restTemplate = new RestTemplate();
            IgdbGameResponse[] igdbGames = restTemplate.postForObject(BASE_URL + "games", new HttpEntity<>(body, headers), IgdbGameResponse[].class);
            if (igdbGames != null && igdbGames.length > 0) {
                showcaseGames.addAll(Arrays.stream(igdbGames).map(this::igdbGameToGame).toList());
            } else {
                showcaseGames.add(defaultGame);
            }
        }
        return showcaseGames;
    }

    @Override
    public Game getGameByName(String name) {
        String body = String.format("fields id, name, summary, cover, url, genres, first_release_date; where name = \"%s\";", name);
        RestTemplate restTemplate = new RestTemplate();
        IgdbGameResponse[] igdbGames = restTemplate.postForObject(BASE_URL + "games", new HttpEntity<>(body, headers), IgdbGameResponse[].class);
        return igdbGames != null && igdbGames.length > 0 ? igdbGameToGame(igdbGames[0]) : defaultGame;
    }

    @Override
    public Game getGameById(Long id) {
        for (Game game : showcaseGames) {
            if (game.getId().equals(id)) {
                return game;
            }
        }

        Optional<Game> gameOpt = gameRepository.findById(id);
        if (gameOpt.isPresent()) {
            return gameOpt.get();
        }

        String body = String.format("fields id, name, summary, cover, url, genres, first_release_date; where id = %d;", id);
        RestTemplate restTemplate = new RestTemplate();
        IgdbGameResponse[] igdbGames = restTemplate.postForObject(BASE_URL + "games", new HttpEntity<>(body, headers), IgdbGameResponse[].class);
        if (igdbGames != null && igdbGames.length > 0) {
            Game game = igdbGameToGame(igdbGames[0]);
            genreRepository.saveAll(game.getGenres());
            gameRepository.save(game);
            return game;
        }

        return defaultGame;
    }

    private void fetchDefaultGame() {
        String body = "fields id, name, summary, cover, url, genres, first_release_date; where name = \"There Is No Game\";";
        RestTemplate restTemplate = new RestTemplate();
        IgdbGameResponse[] igdbGames = restTemplate.postForObject(BASE_URL + "games", new HttpEntity<>(body, headers), IgdbGameResponse[].class);
        if (igdbGames != null && igdbGames.length > 0) {
            defaultGame = igdbGameToGame(igdbGames[0]);
        }
    }

    private Game igdbGameToGame(IgdbGameResponse igdbGame) {
        return new Game(igdbGame.id, igdbGame.name, igdbGame.summary, getCoverById(igdbGame.cover), igdbGame.url, getGenresByIds(igdbGame.genres), TimeMapper.getYearFromUnixTime(igdbGame.releaseDate));
    }

    private String getCoverById(Long id) {
        RestTemplate restTemplate = new RestTemplate();
        String body = String.format("fields url; where id = %d;", id);
        IgdbCoverResponse[] covers = restTemplate.postForObject(BASE_URL + "covers", new HttpEntity<>(body, headers), IgdbCoverResponse[].class);
        return covers != null && covers.length > 0 ? "https:" + covers[0].url.replace("t_thumb", "t_cover_big") : null;
    }

    private List<Genre> getGenresByIds(List<Long> ids) {
        return ids.stream().map(this::getGenreById).filter(Objects::nonNull).toList();
    }

    private Genre getGenreById(Long id) {
        RestTemplate restTemplate = new RestTemplate();
        String body = String.format("fields name; where id = %d;", id);
        Genre[] genres = restTemplate.postForObject(BASE_URL + "genres", new HttpEntity<>(body, headers), Genre[].class);
        return genres != null && genres.length > 0 ? genres[0] : null;
    }

    private String buildAuthUrl() {
        return String.format("https://id.twitch.tv/oauth2/token?client_id=%s&client_secret=%s&grant_type=%s", CLIENT_ID, CLIENT_SECRET, GRANT_TYPE);
    }

    private void generateAccessToken() {
        RestTemplate restTemplate = new RestTemplate();
        IgdbAuthResponse authResponse = restTemplate.postForObject(buildAuthUrl(), null, IgdbAuthResponse.class);
        accessToken = (authResponse != null ? authResponse.accessToken : null);
    }

    private void generateHeaders() {
        headers.set("Client-ID", CLIENT_ID);
        headers.set("Authorization", "Bearer " + accessToken);
    }

    private record IgdbCoverResponse(String url) {
    }

    private record IgdbGameResponse(Long id, String name, String summary, Long cover, String url, List<Long> genres,
                                    @JsonProperty("first_release_date") Long releaseDate) {
    }

    private record IgdbAuthResponse(@JsonProperty("access_token") String accessToken) {
    }
}
