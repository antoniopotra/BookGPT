package software.design.gamegpt.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @ManyToOne
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "users_games_played", joinColumns = {
            @JoinColumn(name = "user_id", referencedColumnName = "id")}, inverseJoinColumns = {
            @JoinColumn(name = "game_id", referencedColumnName = "id")})
    private List<Game> playedGames = new ArrayList<>();

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "users_games_liked", joinColumns = {
            @JoinColumn(name = "user_id", referencedColumnName = "id")}, inverseJoinColumns = {
            @JoinColumn(name = "game_id", referencedColumnName = "id")})
    private List<Game> likedGames = new ArrayList<>();

    public void addPlayedGame(Game game) {
        playedGames.add(game);
    }

    public void removePlayedGame(Game game) {
        playedGames.removeIf(g -> g.getId().equals(game.getId()));
    }

    public boolean hasPlayedGame(Game game) {
        return playedGames.stream().anyMatch(g -> g.getId().equals(game.getId()));
    }

    public void addLikedGame(Game game) {
        likedGames.add(game);
    }

    public void removeLikedGame(Game game) {
        likedGames.removeIf(g -> g.getId().equals(game.getId()));
    }

    public boolean hasLikedGame(Game game) {
        return likedGames.stream().anyMatch(g -> g.getId().equals(game.getId()));
    }
}
