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

import java.util.List;
import java.util.stream.Collectors;

@Controller
public class GameController {
    private final UserService userService;
    private final IgdbService igdbService;
    private final OpenaiService openaiService;

    public GameController(UserService userService, IgdbService igdbService, OpenaiService openaiService) {
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
    public String handlePlayedGame(@PathVariable Long id, Model model) {
        User user = getAuthenticatedUser();
        Game game = igdbService.getGameById(id);
        userService.handlePlayedGame(user, game);
        fillGameDetails(user, game, model);
        return "game";
    }

    @GetMapping("/handleLike/{id}")
    public String handleLikedGame(@PathVariable Long id, Model model) {
        User user = getAuthenticatedUser();
        Game game = igdbService.getGameById(id);
        userService.handleLikedGame(user, game);
        fillGameDetails(user, game, model);
        return "game";
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
        fillGameDetails(getAuthenticatedUser(), igdbService.getGameByName(name), model);
        return "game";
    }

    @GetMapping("/recommendations")
    public String generateRecommendations(Model model) {
        List<String> gameNames = openaiService.getRecommendations(getAuthenticatedUser());
        List<Game> games = gameNames.stream().map(igdbService::getGameByName).toList();
        model.addAttribute("games", games);
        return "recommendations";
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
}
