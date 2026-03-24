package ro.unibuc.prodeng.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ro.unibuc.prodeng.model.Watchlist;
import ro.unibuc.prodeng.service.WatchlistService;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
class WatchlistControllerTest {

    @Mock
    private WatchlistService watchlistService;

    @InjectMocks
    private WatchlistController watchlistController;

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(watchlistController).build();
    }

    @Test
    void testAddToWatchlist_withValidUserIdAndMovieId_returnsWatchlist() throws Exception {
        // Arrange
        String userId = "user-1";
        String movieId = "movie-1";
        Watchlist watchlist = new Watchlist(userId, movieId);

        when(watchlistService.addToWatchlist(userId, movieId)).thenReturn(watchlist);

        // Act & Assert
        mockMvc.perform(post("/api/watchlist")
                        .param("userId", userId)
                        .param("movieId", movieId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(userId))
                .andExpect(jsonPath("$.movieId").value(movieId));

        verify(watchlistService, times(1)).addToWatchlist(userId, movieId);
    }

    @Test
    void testGetUserWatchlist_existingUserRequested_returnsWatchlist() throws Exception {
        // Arrange
        String userId = "user-1";
        List<Watchlist> watchlist = List.of(
                new Watchlist(userId, "movie-1"),
                new Watchlist(userId, "movie-2")
        );

        when(watchlistService.getUserWatchlist(userId)).thenReturn(watchlist);

        // Act & Assert
        mockMvc.perform(get("/api/watchlist/{userId}", userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].userId").value(userId))
                .andExpect(jsonPath("$[0].movieId").value("movie-1"))
                .andExpect(jsonPath("$[1].userId").value(userId))
                .andExpect(jsonPath("$[1].movieId").value("movie-2"));

        verify(watchlistService, times(1)).getUserWatchlist(userId);
    }

    @Test
    void testRemoveFromWatchlist_withValidUserIdAndMovieId_deletesSuccessfully() throws Exception {
        // Arrange
        String userId = "user-1";
        String movieId = "movie-1";

        // Act & Assert
        mockMvc.perform(delete("/api/watchlist")
                        .param("userId", userId)
                        .param("movieId", movieId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(watchlistService, times(1)).removeFromWatchlist(userId, movieId);
    }
}