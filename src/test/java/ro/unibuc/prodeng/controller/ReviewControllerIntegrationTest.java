package ro.unibuc.prodeng.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import ro.unibuc.prodeng.IntegrationTestBase;
import ro.unibuc.prodeng.repository.MovieRepository;
import ro.unibuc.prodeng.repository.ReviewRepository;
import ro.unibuc.prodeng.repository.UserRepository;
import ro.unibuc.prodeng.request.CreateUserRequest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("Review Integration Tests")
public class ReviewControllerIntegrationTest extends IntegrationTestBase {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @BeforeEach
    void cleanUp() {
        reviewRepository.deleteAll();
        movieRepository.deleteAll();
        userRepository.deleteAll();
    }


    private String createUser(String name, String email) throws Exception {
        CreateUserRequest request = new CreateUserRequest(name, email);

        String response = mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        return objectMapper.readTree(response).get("id").asText();
    }

    private String createMovie(String title) throws Exception {
        String body = """
        {
            "title": "%s",
            "description": "test",
            "genreId": "1",
            "releaseDate": "01.01.2020",
            "duration": 100,
            "thumbnailUrl": "x",
            "videoUrl": "x"
        }
        """.formatted(title);

        String response = mockMvc.perform(post("/api/movies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        return objectMapper.readTree(response).get("id").asText();
    }

    private void createReview(String userId, String movieId, String comment) throws Exception {
        String body = """
        {
            "userId": "%s",
            "movieId": "%s",
            "comment": "%s"
        }
        """.formatted(userId, movieId, comment);

        mockMvc.perform(post("/api/reviews")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andExpect(status().isCreated());
    }


    @Test
    void createReview_shouldPersistInDatabase() throws Exception {
        String userId = createUser("Bogdan", "b@mail.com");
        String movieId = createMovie("Jaws");

        String body = """
        {
            "userId": "%s",
            "movieId": "%s",
            "comment": "super film"
        }
        """.formatted(userId, movieId);

        mockMvc.perform(post("/api/reviews")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andExpect(status().isCreated());

        var reviews = reviewRepository.findByMovieId(movieId);

        Assertions.assertEquals(1, reviews.size());
        Assertions.assertEquals("super film", reviews.get(0).getComment());
        Assertions.assertEquals(userId, reviews.get(0).getUserId());
    }

    @Test
    void createReview_shouldNotAllowDuplicate() throws Exception {
        String userId = createUser("Bogdan", "b@mail.com");
        String movieId = createMovie("Jaws");

        createReview(userId, movieId, "first");

        String body = """
        {
            "userId": "%s",
            "movieId": "%s",
            "comment": "second"
        }
        """.formatted(userId, movieId);

        mockMvc.perform(post("/api/reviews")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andExpect(status().is4xxClientError());
    }

    @Test
    void getReviews_shouldReturnCorrectReviews() throws Exception {
        String userId = createUser("Bogdan", "b@mail.com");
        String movieId = createMovie("Jaws");

        createReview(userId, movieId, "nice");

        mockMvc.perform(get("/api/reviews/movie/" + movieId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].comment").value("nice"));
    }

    @Test
    void updateReview_shouldUpdateComment() throws Exception {
        String userId = createUser("Bogdan", "b@mail.com");
        String movieId = createMovie("Jaws");

        String body = """
        {
            "userId": "%s",
            "movieId": "%s",
            "comment": "old"
        }
        """.formatted(userId, movieId);

        String response = mockMvc.perform(post("/api/reviews")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andReturn().getResponse().getContentAsString();

        String reviewId = objectMapper.readTree(response).get("id").asText();

        String updateBody = """
        {
            "userId": "%s",
            "comment": "new"
        }
        """.formatted(userId);

        mockMvc.perform(put("/api/reviews/" + reviewId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateBody))
            .andExpect(status().isOk());

        var review = reviewRepository.findById(reviewId).get();
        Assertions.assertEquals("new", review.getComment());
    }

    @Test
    void deleteReview_shouldRemoveFromDatabase() throws Exception {
        String userId = createUser("Bogdan", "b@mail.com");
        String movieId = createMovie("Jaws");

        String body = """
        {
            "userId": "%s",
            "movieId": "%s",
            "comment": "test"
        }
        """.formatted(userId, movieId);

        String response = mockMvc.perform(post("/api/reviews")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andReturn().getResponse().getContentAsString();

        String reviewId = objectMapper.readTree(response).get("id").asText();

        mockMvc.perform(delete("/api/reviews/" + reviewId + "?userId=" + userId))
            .andExpect(status().isNoContent());

        Assertions.assertTrue(reviewRepository.findById(reviewId).isEmpty());
    }

    @Test
    void updateReview_shouldFail_whenUserIsNotOwner() throws Exception {
        String owner = createUser("Owner", "o@mail.com");
        String other = createUser("Other", "x@mail.com");
        String movieId = createMovie("Jaws");

        String body = """
        {
            "userId": "%s",
            "movieId": "%s",
            "comment": "test"
        }
        """.formatted(owner, movieId);

        String response = mockMvc.perform(post("/api/reviews")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andReturn().getResponse().getContentAsString();

        String reviewId = objectMapper.readTree(response).get("id").asText();

        String updateBody = """
        {
            "userId": "%s",
            "comment": "hack"
        }
        """.formatted(other);

        mockMvc.perform(put("/api/reviews/" + reviewId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateBody))
            .andExpect(status().is4xxClientError());
    }

    @Test
    void deleteReview_shouldFail_whenUserIsNotOwner() throws Exception {
        String owner = createUser("Owner", "o@mail.com");
        String other = createUser("Other", "x@mail.com");
        String movieId = createMovie("Jaws");

        String body = """
        {
            "userId": "%s",
            "movieId": "%s",
            "comment": "test"
        }
        """.formatted(owner, movieId);

        String response = mockMvc.perform(post("/api/reviews")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andReturn().getResponse().getContentAsString();

        String reviewId = objectMapper.readTree(response).get("id").asText();

        mockMvc.perform(delete("/api/reviews/" + reviewId + "?userId=" + other))
            .andExpect(status().is4xxClientError());
    }
}