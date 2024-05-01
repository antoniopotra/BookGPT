package software.design.gamegpt.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import software.design.gamegpt.model.ConfirmationToken;
import software.design.gamegpt.model.Game;
import software.design.gamegpt.model.Genre;
import software.design.gamegpt.model.User;
import software.design.gamegpt.service.EmailService;
import software.design.gamegpt.service.IgdbService;
import software.design.gamegpt.service.OpenaiService;
import software.design.gamegpt.service.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class UserController {
    private final UserService userService;
    private final EmailService emailService;
    private final IgdbService igdbService;
    private final OpenaiService openaiService;

    public UserController(UserService userService, EmailService emailService, IgdbService igdbService, OpenaiService openaiService) {
        this.userService = userService;
        this.emailService = emailService;
        this.igdbService = igdbService;
        this.openaiService = openaiService;
    }

    @GetMapping("/game/{id}")
    public String loadDetailsPage(@PathVariable Long id, Model model) {
        fillGameDetails(userService.getAuthenticatedUser(), igdbService.getGameById(id), model);
        return "game";
    }

    @GetMapping("/handlePlay/{id}")
    public String handlePlayedGame(@PathVariable Long id) {
        userService.handlePlayedGame(userService.getAuthenticatedUser(), igdbService.getGameById(id));
        return "redirect:/game/" + id;
    }

    @GetMapping("/handleLike/{id}")
    public String handleLikedGame(@PathVariable Long id) {
        userService.handleLikedGame(userService.getAuthenticatedUser(), igdbService.getGameById(id));
        return "redirect:/game/" + id;
    }

    @GetMapping("/played")
    public String loadPlayedGames(Model model) {
        model.addAttribute("games", userService.getAuthenticatedUser().getPlayedGames());
        return "played_games";
    }

    @GetMapping("/liked")
    public String loadLikedGames(Model model) {
        model.addAttribute("games", userService.getAuthenticatedUser().getLikedGames());
        return "liked_games";
    }

    @PostMapping("/search")
    public String searchGame(@RequestParam("gameName") String name, Model model) {
        model.addAttribute("games", igdbService.getGamesByName(name));
        return "search_results";
    }

    @GetMapping("/recommendations")
    public String generateRecommendations(Model model) {
        List<String> gameNames = openaiService.getRecommendations(userService.getAuthenticatedUser());
        List<Game> games = new ArrayList<>();
        for (String gameName : gameNames) {
            games.addAll(igdbService.getGamesByName(gameName));
        }
        model.addAttribute("games", games);
        return "recommendations";
    }

    @GetMapping("/upgrade")
    public String requestRoleUpgrade() {
        User user = userService.getAuthenticatedUser();
        ConfirmationToken confirmationToken = new ConfirmationToken(user);

        emailService.saveConfirmationToken(confirmationToken);

        String body = "To become an admin, please click here: http://localhost:8080/confirm-upgrade?token=" + confirmationToken.getConfirmationToken();
        emailService.sendEmail(user.getEmail(), "Become an admin!", body);

        return "redirect:/index";
    }

    @RequestMapping(value = "/confirm-upgrade", method = {RequestMethod.GET, RequestMethod.POST})
    public String confirmUserAccount(@RequestParam("token") String confirmationToken) {
        ConfirmationToken token = emailService.findConfirmationToken(confirmationToken);

        if (token != null) {
            userService.updateRole(token.getUser().getId(), "ROLE_ADMIN");
        } else {
            System.out.println("The link is invalid or broken!");
        }

        return "redirect:/index";
    }

    private void fillGameDetails(User user, Game game, Model model) {
        String genreString = game.getGenres().stream().map(Genre::getName).collect(Collectors.joining(", "));

        model.addAttribute("game", game);
        model.addAttribute("genres", genreString);
        model.addAttribute("played", user.hasPlayedGame(game));
        model.addAttribute("liked", user.hasLikedGame(game));
    }
}
