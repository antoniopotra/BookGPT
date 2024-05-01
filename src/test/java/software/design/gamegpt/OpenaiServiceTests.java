package software.design.gamegpt;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import software.design.gamegpt.service.OpenaiService;
import software.design.gamegpt.service.UserService;

import java.util.List;

@SpringBootTest
public class OpenaiServiceTests {
    @Autowired
    private OpenaiService openaiService;

    @Autowired
    private UserService userService;

    @Test
    public void testRecommendations() {
        List<String> gameNames = openaiService.getRecommendations(userService.findByUsername("test"));
        for (String name : gameNames) {
            assert !name.isEmpty() && !name.isBlank();
        }
    }
}
