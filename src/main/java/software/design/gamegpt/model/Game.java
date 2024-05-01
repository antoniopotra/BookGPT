package software.design.gamegpt.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "games")
public class Game {
    @Id
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "text", nullable = false)
    private String summary;

    @Column(nullable = false)
    private String cover;

    @Column(nullable = false, unique = true)
    private String url;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "games_genres", joinColumns = {
            @JoinColumn(name = "game_id", referencedColumnName = "id")}, inverseJoinColumns = {
            @JoinColumn(name = "genre_id", referencedColumnName = "id")})
    private List<Genre> genres;

    @Column(nullable = false)
    private int year;

    @Column(nullable = false)
    private String category;

    @ManyToMany(mappedBy = "playedGames")
    private List<User> playedBy;

    @ManyToMany(mappedBy = "likedGames")
    private List<User> likedBy;

    public Game() {
    }

    public Game(Long id, String name, String summary, String cover, String url, List<Genre> genres, int year, String category) {
        this.id = id;
        this.name = name;
        this.summary = summary;
        this.cover = cover;
        this.url = url;
        this.genres = genres;
        this.year = year;
        this.category = category;
    }
}
