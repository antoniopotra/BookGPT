package software.design.gamegpt.model;

import jakarta.persistence.*;

import java.util.List;

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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public List<Genre> getGenres() {
        return genres;
    }

    public void setGenres(List<Genre> genres) {
        this.genres = genres;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public List<User> getPlayedBy() {
        return playedBy;
    }

    public void setPlayedBy(List<User> playedBy) {
        this.playedBy = playedBy;
    }

    public List<User> getLikedBy() {
        return likedBy;
    }

    public void setLikedBy(List<User> likedBy) {
        this.likedBy = likedBy;
    }
}
