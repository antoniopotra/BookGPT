package software.design.gamegpt.service;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import software.design.gamegpt.model.Game;
import software.design.gamegpt.model.User;
import software.design.gamegpt.utils.ValidationResult;

import java.util.List;

public interface UserService {
    String EMAIL_PATTERN = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
    String PASSWORD_PATTERN = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*\\W).{8,}$";

    /**
     * Users the SecurityContextHolder to retrieve the currently authenticated user
     *
     * @return the user
     */
    default User getAuthenticatedUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username;
        if (principal instanceof UserDetails) {
            username = ((UserDetails) principal).getUsername();
        } else {
            username = principal.toString();
        }
        return findByUsername(username);
    }

    /**
     * Validates the credentials of a user
     *
     * @param user the user
     * @return a ValidationResult object, contains the status of the validation and possible error messages
     */
    default ValidationResult validateCredentials(User user) {
        String username = user.getUsername();
        String email = user.getEmail();
        String password = user.getPassword();

        if (email == null || email.isEmpty() || email.isBlank() || !email.matches(EMAIL_PATTERN)) {
            return new ValidationResult(false, "email", "invalid_email_format", "Invalid email format.");
        }
        if (password == null || password.isEmpty() || password.isBlank() || !password.matches(PASSWORD_PATTERN)) {
            return new ValidationResult(false, "password", "invalid_password_format", "Password must contain at least 8 characters and one of each: uppercase, lowercase, digit, symbol.");
        }
        if (findByUsername(username) != null) {
            return new ValidationResult(false, "username", "username_exists", "Username already in use.");
        }
        if (findByEmail(email) != null) {
            return new ValidationResult(false, "email", "email_exists", "Email already in use.");
        }
        return new ValidationResult(true, "", "", "");
    }

    void save(User user);

    User findByUsername(String username);

    User findByEmail(String email);

    List<User> findAll();

    /**
     * Adds or removes a game from the list of played games of a user
     * If it is removed as played, it is also removed as liked
     *
     * @param user the current user
     * @param game the selected game
     */
    void handlePlayedGame(User user, Game game);

    /**
     * Adds or removes a game from the list of liked games of a user
     * If it is added as liked, it is also added as played
     *
     * @param user the current user
     * @param game the selected game
     */
    void handleLikedGame(User user, Game game);

    void deleteById(Long id);

    /**
     * Changes the role of a user
     *
     * @param userId the id of the user
     * @param role   the name of the new role
     */
    void updateRole(Long userId, String role);
}
