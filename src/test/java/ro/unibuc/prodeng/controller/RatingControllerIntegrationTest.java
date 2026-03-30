package ro.unibuc.prodeng.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import ro.unibuc.prodeng.IntegrationTestBase;
import ro.unibuc.prodeng.repository.MovieRepository;
import ro.unibuc.prodeng.repository.RatingRepository;
import ro.unibuc.prodeng.repository.UserRepository;
import ro.unibuc.prodeng.request.CreateUserRequest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("Rating Integration Tests")
public class RatingControllerIntegrationTest extends IntegrationTestBase {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private RatingRepository ratingRepository;

    @BeforeEach
    void cleanUp() {
        ratingRepository.deleteAll();
        movieRepository.deleteAll();
        userRepository.deleteAll();
    }


    private String createUser(String name, String email) throws Exception {
        CreateUserRequest request = new CreateUserRequest(name, email);

        String response = mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

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
                .andReturn()
                .getResponse()
                .getContentAsString();

        return objectMapper.readTree(response).get("id").asText();
    }

    private void createRating(String userId, String movieId, int value) throws Exception {
        String body = """
        {
            "userId": "%s",
            "movieId": "%s",
            "value": %d
        }
        """.formatted(userId, movieId, value);

        mockMvc.perform(post("/api/ratings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andExpect(status().isCreated());
    }


    @Test
    void createRating_shouldPersistInDatabase() throws Exception {
        // arrange
        String userId = createUser("Bogdan", "bogdan@mail.com");
        String movieId = createMovie("Jaws");

        String body = """
        {
            "userId": "%s",
            "movieId": "%s",
            "value": 5
        }
        """.formatted(userId, movieId);

        // act
        mockMvc.perform(post("/api/ratings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andExpect(status().isCreated());

        // ASSERT 
        var ratings = ratingRepository.findByMovieId(movieId);

        Assertions.assertEquals(1, ratings.size());
        Assertions.assertEquals(5, ratings.get(0).getValue());
        Assertions.assertEquals(userId, ratings.get(0).getUserId());
        Assertions.assertEquals(movieId, ratings.get(0).getMovieId());
    }

    @Test
    void createRating_shouldNotAllowDuplicateRating() throws Exception {
        String userId = createUser("Bogdan", "bogdan@mail.com");
        String movieId = createMovie("Jaws");

        createRating(userId, movieId, 5);

        String body = """
        {
            "userId": "%s",
            "movieId": "%s",
            "value": 4
        }
        """.formatted(userId, movieId);

        mockMvc.perform(post("/api/ratings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andExpect(status().is4xxClientError());
    }

    @Test
    void getRatingsForMovie_shouldReturnCorrectRatings() throws Exception {
        String userId = createUser("Bogdan", "bogdan@mail.com");
        String movieId = createMovie("Jaws");

        createRating(userId, movieId, 5);

        mockMvc.perform(get("/api/ratings/movie/" + movieId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].value").value(5))
            .andExpect(jsonPath("$[0].userId").value(userId));
    }


    @Test
    void updateRating_shouldUpdateValueInDatabase() throws Exception {
        String userId = createUser("Bogdan", "bogdan@mail.com");
        String movieId = createMovie("Jaws");

        // create rating
        String body = """
        {
            "userId": "%s",
            "movieId": "%s",
            "value": 5
        }
        """.formatted(userId, movieId);

        String response = mockMvc.perform(post("/api/ratings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andReturn().getResponse().getContentAsString();

        String ratingId = objectMapper.readTree(response).get("id").asText();

        // update rating
        String updateBody = """
        {
            "userId": "%s",
            "value": 2
        }
        """.formatted(userId);

        mockMvc.perform(put("/api/ratings/" + ratingId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateBody))
            .andExpect(status().isOk());

        //DB check
        var rating = ratingRepository.findById(ratingId).get();
        Assertions.assertEquals(2, rating.getValue());
    }


    @Test
    void deleteRating_shouldRemoveFromDatabase() throws Exception {
        String userId = createUser("Bogdan", "bogdan@mail.com");
        String movieId = createMovie("Jaws");

        String body = """
        {
            "userId": "%s",
            "movieId": "%s",
            "value": 5
        }
        """.formatted(userId, movieId);

        String response = mockMvc.perform(post("/api/ratings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andReturn().getResponse().getContentAsString();

        String ratingId = objectMapper.readTree(response).get("id").asText();

        // delete
        mockMvc.perform(delete("/api/ratings/" + ratingId + "?userId=" + userId))
            .andExpect(status().isNoContent());

        //  DB check
        Assertions.assertTrue(ratingRepository.findById(ratingId).isEmpty());
    }


    @Test
    void updateRating_shouldFail_whenUserIsNotOwner() throws Exception {
        String ownerId = createUser("Owner", "o@mail.com");
        String otherUser = createUser("Hacker", "h@mail.com");
        String movieId = createMovie("Jaws");

        // owner creează rating
        String body = """
        {
            "userId": "%s",
            "movieId": "%s",
            "value": 5
        }
        """.formatted(ownerId, movieId);

        String response = mockMvc.perform(post("/api/ratings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andReturn().getResponse().getContentAsString();

        String ratingId = objectMapper.readTree(response).get("id").asText();

        // alt user încearcă update
        String updateBody = """
        {
            "userId": "%s",
            "value": 1
        }
        """.formatted(otherUser);

        mockMvc.perform(put("/api/ratings/" + ratingId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateBody))
            .andExpect(status().is4xxClientError());
    }



    @Test
    void deleteRating_shouldFail_whenUserIsNotOwner() throws Exception {
        String ownerId = createUser("Owner", "o@mail.com");
        String otherUser = createUser("Hacker", "h@mail.com");
        String movieId = createMovie("Jaws");

        String body = """
        {
            "userId": "%s",
            "movieId": "%s",
            "value": 5
        }
        """.formatted(ownerId, movieId);

        String response = mockMvc.perform(post("/api/ratings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andReturn().getResponse().getContentAsString();

        String ratingId = objectMapper.readTree(response).get("id").asText();

        // alt user încearcă delete
        mockMvc.perform(delete("/api/ratings/" + ratingId + "?userId=" + otherUser))
            .andExpect(status().is4xxClientError());
    }
}
