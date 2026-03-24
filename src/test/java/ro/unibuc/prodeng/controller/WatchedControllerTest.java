package ro.unibuc.prodeng.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ro.unibuc.prodeng.model.Watched;
import ro.unibuc.prodeng.service.WatchedService;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
class WatchedControllerTest {

    @Mock
    private WatchedService watchedService;

    @InjectMocks
    private WatchedController watchedController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(watchedController).build();
    }

    @Test
    void testUpdateProgress_withValidData_returnsUpdatedWatched() throws Exception {
        // Arrange
        String userId = "user-1";
        String movieId = "movie-1";
        Integer lastTime = 45;
        Watched watched = new Watched(userId, movieId, lastTime);

        when(watchedService.updateProgress(userId, movieId, lastTime)).thenReturn(watched);

        // Act & Assert
        mockMvc.perform(post("/api/watched/progress")
                        .param("userId", userId)
                        .param("movieId", movieId)
                        .param("lastTime", String.valueOf(lastTime))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(userId))
                .andExpect(jsonPath("$.movieId").value(movieId))
                .andExpect(jsonPath("$.lastTime").value(lastTime));

        verify(watchedService, times(1)).updateProgress(userId, movieId, lastTime);
    }

    @Test
    void testGetAllWatchedForUser_existingUserRequested_returnsWatchedList() throws Exception {
        // Arrange
        String userId = "user-1";
        List<Watched> watchedList = List.of(
                new Watched(userId, "movie-1", 120),
                new Watched(userId, "movie-2", 95)
        );

        when(watchedService.getAllWatchedForUser(userId)).thenReturn(watchedList);

        // Act & Assert
        mockMvc.perform(get("/api/watched/{userId}", userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].userId").value(userId))
                .andExpect(jsonPath("$[0].movieId").value("movie-1"))
                .andExpect(jsonPath("$[0].lastTime").value(120))
                .andExpect(jsonPath("$[1].userId").value(userId))
                .andExpect(jsonPath("$[1].movieId").value("movie-2"))
                .andExpect(jsonPath("$[1].lastTime").value(95));

        verify(watchedService, times(1)).getAllWatchedForUser(userId);
    }

    @Test
    void testGetContinueWatching_existingUserRequested_returnsContinueWatchingList() throws Exception {
        // Arrange
        String userId = "user-1";
        List<Watched> watchedList = List.of(
                new Watched(userId, "movie-1", 30),
                new Watched(userId, "movie-2", 70)
        );

        when(watchedService.getContinueWatching(userId)).thenReturn(watchedList);

        // Act & Assert
        mockMvc.perform(get("/api/watched/continue/{userId}", userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].userId").value(userId))
                .andExpect(jsonPath("$[0].movieId").value("movie-1"))
                .andExpect(jsonPath("$[0].lastTime").value(30))
                .andExpect(jsonPath("$[1].userId").value(userId))
                .andExpect(jsonPath("$[1].movieId").value("movie-2"))
                .andExpect(jsonPath("$[1].lastTime").value(70));

        verify(watchedService, times(1)).getContinueWatching(userId);
    }
}