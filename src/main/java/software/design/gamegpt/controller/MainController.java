package software.design.gamegpt.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import software.design.gamegpt.model.Game;
import software.design.gamegpt.model.Genre;
import software.design.gamegpt.model.User;
import software.design.gamegpt.service.IgdbService;
import software.design.gamegpt.service.UserService;

import java.util.*;

@Controller
public class MainController {
    private final UserService userService;
    private final IgdbService igdbService;

    public MainController(UserService userService, IgdbService igdbService) {
        this.userService = userService;
        this.igdbService = igdbService;
    }

    @GetMapping("/index")
    public String home(Model model) {
        if (userService.getAuthenticatedUser().getRole().getName().equals("ROLE_USER")) {
            model.addAttribute("games", igdbService.getShowcaseGames());
            return "user_index";
        }
        return "admin_index";
    }

    @GetMapping("/stats")
    public String generateStats(Model model) {
        User user = userService.getAuthenticatedUser();

        if (user.getRole().getName().equals("ROLE_USER")) {
            model.addAttribute("playedGenreStats", generateStats(StatsFilter.GENRE, user.getPlayedGames()));
            model.addAttribute("playedYearStats", generateStats(StatsFilter.YEAR, user.getPlayedGames()));
            model.addAttribute("likedGenreStats", generateStats(StatsFilter.GENRE, user.getLikedGames()));
            model.addAttribute("likedYearStats", generateStats(StatsFilter.YEAR, user.getLikedGames()));
            return "user_stats";
        }

        model.addAttribute("usersStats", generateStats(StatsFilter.USERS, null));
        return "admin_stats";
    }

    private List<List<Object>> generateStats(StatsFilter filter, List<Game> games) {
        Map<String, Integer> count = new HashMap<>();

        switch (filter) {
            case YEAR -> {
                for (Game game : games) {
                    String year = String.valueOf(game.getYear());
                    count.put(year, count.getOrDefault(year, 0) + 1);
                }
            }
            case GENRE -> {
                for (Game game : games) {
                    for (Genre genre : game.getGenres()) {
                        count.put(genre.getName(), count.getOrDefault(genre.getName(), 0) + 1);
                    }
                }
            }
            case USERS -> {
                for (User user : userService.findAll()) {
                    String role = user.getRole().getName().split("_")[1];
                    count.put(role, count.getOrDefault(role, 0) + 1);
                }
            }
        }

        List<List<Object>> result = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : count.entrySet()) {
            result.add(Arrays.asList(entry.getKey(), entry.getValue()));
        }

        return result;
    }

    private enum StatsFilter {
        YEAR,
        GENRE,
        USERS
    }
}
