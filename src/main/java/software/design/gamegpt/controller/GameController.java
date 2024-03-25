package software.design.gamegpt.controller;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import software.design.gamegpt.model.Game;
import software.design.gamegpt.model.Genre;
import software.design.gamegpt.model.User;
import software.design.gamegpt.service.GameService;
import software.design.gamegpt.service.UserService;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class GameController {
    private final UserService userService;
    private final GameService gameService;
    private final Map<Long, Game> games = new HashMap<>();

    public GameController(UserService userService, GameService gameService) {
        this.userService = userService;
        this.gameService = gameService;
    }

    @GetMapping("/index")
    public String home(Model model) {
        if (games.isEmpty()) {
            gameService.getGames().forEach(game ->
                    games.put(game.getId(), game)
            );
        }
        model.addAttribute("games", games.values());
        return "index";
    }

    @GetMapping("/game/{id}")
    public String loadDetailsPage(@PathVariable Long id, Model model) {
        Game game = games.get(id);
        String genreString = game.getGenres().stream().map(Genre::getName).collect(Collectors.joining(", "));
        boolean played = getAuthenticatedUser().hasPlayedGame(game);
        boolean liked = getAuthenticatedUser().hasLikedGame(game);

        model.addAttribute("game", game);
        model.addAttribute("genres", genreString);
        model.addAttribute("played", played);
        model.addAttribute("liked", liked);
        return "game";
    }

    @GetMapping("/handlePlay/{id}")
    public String handlePlayedGame(@PathVariable Long id) {
        Game game = games.get(id);
        userService.handlePlayedGame(getAuthenticatedUser(), game);
        return "redirect:/game/" + id;
    }

    @GetMapping("/handleLike/{id}")
    public String handleLikedGame(@PathVariable Long id) {
        Game game = games.get(id);
        userService.handleLikedGame(getAuthenticatedUser(), game);
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
}
