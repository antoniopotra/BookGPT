package software.design.gamegpt;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import software.design.gamegpt.service.UserService;

@SpringBootTest
public class UserServiceTests {
    @Autowired
    private UserService userService;

    @Test
    public void findExistingUser() {
        assert userService.findByUsername("admin").getUsername().equals("admin");
        assert userService.findByEmail("antonio.potra@gmail.com").getEmail().equals("antonio.potra@gmail.com");
    }

    @Test
    public void findNonExistingUser() {
        assert userService.findByUsername("a username which surely does not exist") == null;
    }
}