package software.design.gamegpt.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import software.design.gamegpt.repository.IgdbRepository;

@Controller
public class GamesController {
    private final IgdbRepository igdbRepository = new IgdbRepository();

    @GetMapping("/index")
    public String home() {
//        for (Game game : igdbRepository.getGames()) {
//            System.out.println(game.name());
//        }
        System.out.println(igdbRepository.getGames());
        return "index";
    }
}
