package software.design.gamegpt.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import software.design.gamegpt.model.Game;
import software.design.gamegpt.service.GameService;

import java.util.ArrayList;
import java.util.List;

@Controller
public class GamesController {
    private final GameService gameService;
    private final List<Game> games = new ArrayList<>();

    public GamesController(GameService gameService) {
        this.gameService = gameService;
    }

    @GetMapping("/index")
    public String home(Model model) {
        if (games.isEmpty()) {
            games.addAll(gameService.getGames());
            games.add(gameService.getGameByName("Cuphead"));
        }
        model.addAttribute("games", games);
        return "index";
    }
}
