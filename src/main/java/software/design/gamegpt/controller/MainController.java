package software.design.gamegpt.controller;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import software.design.gamegpt.model.Game;
import software.design.gamegpt.model.Genre;
import software.design.gamegpt.model.User;
import software.design.gamegpt.service.IgdbService;
import software.design.gamegpt.service.OpenaiService;
import software.design.gamegpt.service.UserService;

import java.util.*;
import java.util.stream.Collectors;

@Controller
public class MainController {
    private final UserService userService;
    private final IgdbService igdbService;
    private final OpenaiService openaiService;

    public MainController(UserService userService, IgdbService igdbService, OpenaiService openaiService) {
        this.userService = userService;
        this.igdbService = igdbService;
        this.openaiService = openaiService;
    }

    @GetMapping("/index")
    public String home(Model model) {
        model.addAttribute("games", igdbService.getShowcaseGames());
        return "index";
    }

    @GetMapping("/game/{id}")
    public String loadDetailsPage(@PathVariable Long id, Model model) {
        fillGameDetails(getAuthenticatedUser(), igdbService.getGameById(id), model);
        return "game";
    }

    @GetMapping("/handlePlay/{id}")
    public String handlePlayedGame(@PathVariable Long id) {
        userService.handlePlayedGame(getAuthenticatedUser(), igdbService.getGameById(id));
        return "redirect:/game/" + id;
    }

    @GetMapping("/handleLike/{id}")
    public String handleLikedGame(@PathVariable Long id) {
        userService.handleLikedGame(getAuthenticatedUser(), igdbService.getGameById(id));
        return "redirect:/game/" + id;
    }

    @GetMapping("/played")
    public String loadPlayedGames(Model model) {
        model.addAttribute("games", getAuthenticatedUser().getPlayedGames());
        return "played_games";
    }

    @GetMapping("/liked")
    public String loadLikedGames(Model model) {
        model.addAttribute("games", getAuthenticatedUser().getLikedGames());
        return "liked_games";
    }

    @PostMapping("/search")
    public String searchGame(@RequestParam("gameName") String name, Model model) {
        model.addAttribute("games", igdbService.getGamesByName(name));
        return "search_results";
    }

    @GetMapping("/recommendations")
    public String generateRecommendations(Model model) {
        List<String> gameNames = openaiService.getRecommendations(getAuthenticatedUser());
        List<Game> games = new ArrayList<>();
        for (String gameName : gameNames) {
            games.addAll(igdbService.getGamesByName(gameName));
        }
        model.addAttribute("games", games);
        return "recommendations";
    }

    @GetMapping("/stats")
    public String generateStats(Model model) {
        model.addAttribute("playedGenreStats", generateStats(getAuthenticatedUser().getPlayedGames(), StatsFilter.GENRE));
        model.addAttribute("playedYearStats", generateStats(getAuthenticatedUser().getPlayedGames(), StatsFilter.YEAR));
        model.addAttribute("likedGenreStats", generateStats(getAuthenticatedUser().getLikedGames(), StatsFilter.GENRE));
        model.addAttribute("likedYearStats", generateStats(getAuthenticatedUser().getLikedGames(), StatsFilter.YEAR));
        return "stats";
    }

    private List<List<Object>> generateStats(List<Game> games, StatsFilter filter) {
        Map<String, Integer> count = new HashMap<>();

        for (Game game : games) {
            switch (filter) {
                case YEAR -> count.put(String.valueOf(game.getYear()),
                        count.getOrDefault(String.valueOf(game.getYear()), 0) + 1);
                case GENRE -> {
                    for (Genre genre : game.getGenres()) {
                        count.put(genre.getName(), count.getOrDefault(genre.getName(), 0) + 1);
                    }
                }
            }
        }

        List<List<Object>> result = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : count.entrySet()) {
            result.add(Arrays.asList(entry.getKey(), entry.getValue()));
        }

        return result;
    }

    private User getAuthenticatedUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username;
        if (principal instanceof UserDetails) {
            username = ((UserDetails) principal).getUsername();
        } else {
            username = principal.toString();
        }
        return userService.findByUsername(username);
    }

    private void fillGameDetails(User user, Game game, Model model) {
        String genreString = game.getGenres().stream().map(Genre::getName).collect(Collectors.joining(", "));

        model.addAttribute("game", game);
        model.addAttribute("genres", genreString);
        model.addAttribute("played", user.hasPlayedGame(game));
        model.addAttribute("liked", user.hasLikedGame(game));
    }

    private enum StatsFilter {
        YEAR,
        GENRE
    }
}
