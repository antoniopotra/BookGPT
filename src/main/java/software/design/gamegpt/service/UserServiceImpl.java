package software.design.gamegpt.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import software.design.gamegpt.model.Game;
import software.design.gamegpt.model.Role;
import software.design.gamegpt.model.User;
import software.design.gamegpt.repository.GameRepository;
import software.design.gamegpt.repository.RoleRepository;
import software.design.gamegpt.repository.UserRepository;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final GameRepository gameRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository,
                           RoleRepository roleRepository,
                           GameRepository gameRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.gameRepository = gameRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void save(User user) {
        Role role = roleRepository.findByName("ROLE_USER");
        if (role == null) {
            role = createRoles();
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.addRole(role);
        userRepository.save(user);
    }

    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public void addPlayedGame(User user, Game game) {
        if (!user.hasPlayedGame(game)) {
            if (!gameRepository.existsById(game.getId())) {
                gameRepository.save(game);
            }
            userRepository.addPlayedGame(user.getId(), game.getId());
        }
    }

    @Override
    public void addLikedGame(User user, Game game) {
        if (!user.hasLikedGame(game)) {
            if (!gameRepository.existsById(game.getId())) {
                gameRepository.save(game);
            }
            userRepository.addLikedGame(user.getId(), game.getId());
            addPlayedGame(user, game);
        }
    }

    @Override
    public void removePlayedGame(User user, Game game) {
        if (user.hasPlayedGame(game)) {
            userRepository.removePlayedGame(user.getId(), game.getId());
            removeLikedGame(user, game);
        }
    }

    @Override
    public void removeLikedGame(User user, Game game) {
        if (user.hasLikedGame(game)) {
            userRepository.removeLikedGame(user.getId(), game.getId());
        }
    }

    private Role createRoles() {
        Role admin = new Role();
        admin.setName("ROLE_ADMIN");
        roleRepository.save(admin);

        Role user = new Role();
        user.setName("ROLE_USER");
        roleRepository.save(user);
        return user;
    }
}