package software.design.gamegpt.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import software.design.gamegpt.model.Game;
import software.design.gamegpt.model.Role;
import software.design.gamegpt.model.User;
import software.design.gamegpt.repository.GameRepository;
import software.design.gamegpt.repository.GenreRepository;
import software.design.gamegpt.repository.RoleRepository;
import software.design.gamegpt.repository.UserRepository;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final GameRepository gameRepository;
    private final GenreRepository genreRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository,
                           RoleRepository roleRepository,
                           GameRepository gameRepository,
                           GenreRepository genreRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.gameRepository = gameRepository;
        this.genreRepository = genreRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void save(User user) {
        Role role = roleRepository.findByName("ROLE_USER");
        if (role == null) {
            role = createRoles();
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(role);
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
    public void handlePlayedGame(User user, Game game) {
        if (user.hasPlayedGame(game)) {
            removePlayedGame(user, game);
            if (user.hasLikedGame(game)) {
                removeLikedGame(user, game);
            }
        } else {
            addPlayedGame(user, game);
        }
    }

    @Override
    public void handleLikedGame(User user, Game game) {
        if (user.hasLikedGame(game)) {
            removeLikedGame(user, game);
        } else {
            addLikedGame(user, game);
            if (!user.hasPlayedGame(game)) {
                addPlayedGame(user, game);
            }
        }
    }

    private void addPlayedGame(User user, Game game) {
        genreRepository.saveAll(game.getGenres());
        gameRepository.save(game);
        user.addPlayedGame(game);
        userRepository.save(user);
    }

    private void addLikedGame(User user, Game game) {
        genreRepository.saveAll(game.getGenres());
        gameRepository.save(game);
        user.addLikedGame(game);
        userRepository.save(user);
    }

    private void removePlayedGame(User user, Game game) {
        user.removePlayedGame(game);
        userRepository.save(user);
    }

    private void removeLikedGame(User user, Game game) {
        user.removeLikedGame(game);
        userRepository.save(user);
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