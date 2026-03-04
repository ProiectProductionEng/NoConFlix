package ro.unibuc.prodeng.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.CompoundIndex;

import java.time.Instant;

@Document(collection = "watched")
@CompoundIndex(name = "uniq_user_movie_watched", def = "{'userId': 1, 'movieId': 1}", unique = true)
public class Watched {

    @Id
    private String id;

    private String userId;
    private String movieId;

    private Integer lastTime;

    private Instant updatedAt;

    public Watched() {}

    public Watched(String userId, String movieId, Integer lastTime) {
        this.userId = userId;
        this.movieId = movieId;
        this.lastTime = lastTime;
        this.updatedAt = Instant.now();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getMovieId() { return movieId; }
    public void setMovieId(String movieId) { this.movieId = movieId; }

    public Integer getLastTime() { return lastTime; }
    public void setLastTime(Integer lastTime) { 
        this.lastTime = lastTime; 
    }

    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
}