package software.design.gamegpt;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import software.design.gamegpt.model.Game;
import software.design.gamegpt.service.IgdbService;

import java.util.List;

@SpringBootTest
public class IgdbServiceTests {
    @Autowired
    private IgdbService igdbService;

    @Test
    public void testShowcaseGames() {
        List<Game> games = igdbService.getShowcaseGames();
        assert games.size() == 12;
        for (Game game : games) {
            assert game.getCategory().equals("main game");
        }
    }

    @Test
    public void testSearchByExistingName() {
        List<Game> games = igdbService.getGamesByName("Hades");
        assert games.size() == 2;
        for (Game game : games) {
            assert game.getName().equals("Hades");
        }
    }

    @Test
    public void testSearchByNonExistingName() {
        List<Game> games = igdbService.getGamesByName("the name of a game which surely does not exist");
        assert games.size() == 1;
        assert games.getFirst().getName().equals("There Is No Game");
    }
}
