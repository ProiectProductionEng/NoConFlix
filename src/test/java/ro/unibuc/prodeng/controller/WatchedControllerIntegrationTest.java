package ro.unibuc.prodeng.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import ro.unibuc.prodeng.IntegrationTestBase;
import ro.unibuc.prodeng.repository.MovieRepository;
import ro.unibuc.prodeng.repository.WatchedRepository;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("Watched Integration Tests")
public class WatchedControllerIntegrationTest extends IntegrationTestBase {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private WatchedRepository watchedRepository;

    @Autowired
    private MovieRepository movieRepository;

    @BeforeEach
    void cleanUp() {
        watchedRepository.deleteAll();
        movieRepository.deleteAll();
    }

    private String createMovie(String title, int duration) throws Exception {
        String body = """
        {
            "title": "%s",
            "description": "test movie",
            "genreId": "1",
            "releaseDate": "01.01.2020",
            "duration": %d,
            "thumbnailUrl": "x",
            "videoUrl": "x"
        }
        """.formatted(title, duration);

        String response = mockMvc.perform(post("/api/movies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        return objectMapper.readTree(response).get("id").asText();
    }

    private void updateProgress(String userId, String movieId, int lastTime) throws Exception {
        mockMvc.perform(post("/api/watched/progress")
                        .param("userId", userId)
                        .param("movieId", movieId)
                        .param("lastTime", String.valueOf(lastTime)))
                .andExpect(status().isOk());
    }

    @Test
    void updateProgress_shouldCreateWatchedEntryWhenNotExists() throws Exception {
        String movieId = createMovie("Movie 1", 120);

        mockMvc.perform(post("/api/watched/progress")
                        .param("userId", "user1")
                        .param("movieId", movieId)
                        .param("lastTime", "30"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.userId").value("user1"))
                .andExpect(jsonPath("$.movieId").value(movieId))
                .andExpect(jsonPath("$.lastTime").value(30))
                .andExpect(jsonPath("$.updatedAt").exists());

        var watchedList = watchedRepository.findByUserIdOrderByUpdatedAtDesc("user1");
        org.junit.jupiter.api.Assertions.assertEquals(1, watchedList.size());
        org.junit.jupiter.api.Assertions.assertEquals(30, watchedList.get(0).getLastTime());
    }

    @Test
    void updateProgress_shouldUpdateExistingWatchedEntry() throws Exception {
        String movieId = createMovie("Movie 1", 120);

        updateProgress("user1", movieId, 30);
        updateProgress("user1", movieId, 50);

        var watchedList = watchedRepository.findByUserIdOrderByUpdatedAtDesc("user1");
        org.junit.jupiter.api.Assertions.assertEquals(1, watchedList.size());
        org.junit.jupiter.api.Assertions.assertEquals(50, watchedList.get(0).getLastTime());
    }

    @Test
    void getContinueWatching_shouldReturnOnlyUnfinishedMovies() throws Exception {
        String movie1 = createMovie("Movie 1", 120);
        String movie2 = createMovie("Movie 2", 90);
        String movie3 = createMovie("Movie 3", 150);

        updateProgress("user1", movie1, 30);
        updateProgress("user1", movie2, 90);
        updateProgress("user1", movie3, 148);

        mockMvc.perform(get("/api/watched/continue/user1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void getAllWatchedForUser_shouldReturnOnlyAlmostFinishedOrFinishedMovies() throws Exception {
        String movie1 = createMovie("Movie 1", 120);
        String movie2 = createMovie("Movie 2", 90);
        String movie3 = createMovie("Movie 3", 150);

        updateProgress("user1", movie1, 30);
        updateProgress("user1", movie2, 90);
        updateProgress("user1", movie3, 148);

        mockMvc.perform(get("/api/watched/user1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }
}