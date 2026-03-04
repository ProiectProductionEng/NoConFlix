package ro.unibuc.prodeng.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.CompoundIndex;

import java.time.Instant;

@Document(collection = "watchlists")
@CompoundIndex(name = "uniq_user_movie_watchlist", def = "{'userId': 1, 'movieId': 1}", unique = true)
public class Watchlist {

    @Id
    private String id;

    private String userId;
    private String movieId;

    private Instant createdAt;

    public Watchlist() {}

    public Watchlist(String userId, String movieId) {
        this.userId = userId;
        this.movieId = movieId;
        this.createdAt = Instant.now();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getMovieId() { return movieId; }
    public void setMovieId(String movieId) { this.movieId = movieId; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}