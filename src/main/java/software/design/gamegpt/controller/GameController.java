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

import java.util.stream.Collectors;

@Controller
public class GameController {
    private final UserService userService;
    private final GameService gameService;

    public GameController(UserService userService, GameService gameService) {
        this.userService = userService;
        this.gameService = gameService;
    }

    @GetMapping("/index")
    public String home(Model model) {
        model.addAttribute("games", gameService.getShowcaseGames());
        return "index";
    }

    @GetMapping("/game/{id}")
    public String loadDetailsPage(@PathVariable Long id, Model model) {
        User user = getAuthenticatedUser();
        Game game = gameService.getGameById(id);
        String genreString = game.getGenres().stream().map(Genre::getName).collect(Collectors.joining(", "));

        model.addAttribute("game", game);
        model.addAttribute("genres", genreString);
        model.addAttribute("played", user.hasPlayedGame(game));
        model.addAttribute("liked", user.hasLikedGame(game));
        return "game";
    }

    @GetMapping("/handlePlay/{id}")
    public String handlePlayedGame(@PathVariable Long id) {
        userService.handlePlayedGame(getAuthenticatedUser(), gameService.getGameById(id));
        return "redirect:/game/" + id;
    }

    @GetMapping("/handleLike/{id}")
    public String handleLikedGame(@PathVariable Long id) {
        userService.handleLikedGame(getAuthenticatedUser(), gameService.getGameById(id));
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
