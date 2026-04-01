package ro.unibuc.prodeng.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

import ro.unibuc.prodeng.IntegrationTestBase;
import ro.unibuc.prodeng.repository.WatchlistRepository;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("Watchlist Integration Tests")
public class WatchlistControllerIntegrationTest extends IntegrationTestBase {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WatchlistRepository watchlistRepository;

    @BeforeEach
    void cleanUp() {
        watchlistRepository.deleteAll();
    }

    private void addToWatchlist(String userId, String movieId) throws Exception {
        mockMvc.perform(post("/api/watchlist")
                        .param("userId", userId)
                        .param("movieId", movieId))
                .andExpect(status().isOk());
    }

    @Test
    void addToWatchlist_shouldPersistInDatabase() throws Exception {
        String userId = "user1";
        String movieId = "movie1";

        mockMvc.perform(post("/api/watchlist")
                        .param("userId", userId)
                        .param("movieId", movieId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.userId").value(userId))
                .andExpect(jsonPath("$.movieId").value(movieId))
                .andExpect(jsonPath("$.createdAt").exists());

        var watchlist = watchlistRepository.findByUserIdOrderByCreatedAtDesc(userId);

        org.junit.jupiter.api.Assertions.assertEquals(1, watchlist.size());
        org.junit.jupiter.api.Assertions.assertEquals(userId, watchlist.get(0).getUserId());
        org.junit.jupiter.api.Assertions.assertEquals(movieId, watchlist.get(0).getMovieId());
        org.junit.jupiter.api.Assertions.assertNotNull(watchlist.get(0).getCreatedAt());
    }

    @Test
    void addToWatchlist_shouldNotAllowDuplicateMovieForSameUser() throws Exception {
        String userId = "user1";
        String movieId = "movie1";

        addToWatchlist(userId, movieId);

        mockMvc.perform(post("/api/watchlist")
                        .param("userId", userId)
                        .param("movieId", movieId))
                .andExpect(status().is4xxClientError());

        var watchlist = watchlistRepository.findByUserIdOrderByCreatedAtDesc(userId);
        org.junit.jupiter.api.Assertions.assertEquals(1, watchlist.size());
    }

    @Test
    void getUserWatchlist_shouldReturnOnlyRequestedUserMovies() throws Exception {
        addToWatchlist("user1", "movie1");
        addToWatchlist("user1", "movie2");
        addToWatchlist("user2", "movie3");

        mockMvc.perform(get("/api/watchlist/user1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].userId").value("user1"))
                .andExpect(jsonPath("$[1].userId").value("user1"));
    }

    @Test
    void removeFromWatchlist_shouldRemoveEntryFromDatabase() throws Exception {
        addToWatchlist("user1", "movie1");
        addToWatchlist("user1", "movie2");

        mockMvc.perform(delete("/api/watchlist")
                        .param("userId", "user1")
                        .param("movieId", "movie1"))
                .andExpect(status().isOk());

        var watchlist = watchlistRepository.findByUserIdOrderByCreatedAtDesc("user1");

        org.junit.jupiter.api.Assertions.assertEquals(1, watchlist.size());
        org.junit.jupiter.api.Assertions.assertEquals("movie2", watchlist.get(0).getMovieId());
    }
}