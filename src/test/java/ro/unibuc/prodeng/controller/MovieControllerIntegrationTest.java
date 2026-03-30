package ro.unibuc.prodeng.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import ro.unibuc.prodeng.IntegrationTestBase;
import ro.unibuc.prodeng.repository.MovieRepository;
import ro.unibuc.prodeng.repository.RatingRepository;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.bson.Document;

@DisplayName("Movie Integration Tests")
public class MovieControllerIntegrationTest extends IntegrationTestBase {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private RatingRepository ratingRepository;

    @Autowired
    MongoTemplate mongoTemplate;

    @BeforeEach
    void setUp() {
        ratingRepository.deleteAll();
        movieRepository.deleteAll();

        mongoTemplate.getCollection("movies")
            .createIndex(new Document("title", "text")
            .append("description", "text"));
    }


    private String createMovie(String title, int views) throws Exception {
        String body = """
        {
            "title": "%s",
            "description": "test movie",
            "genreId": "1",
            "releaseDate": "01.01.2020",
            "duration": 100,
            "totalViews": %d,
            "thumbnailUrl": "x",
            "videoUrl": "x"
        }
        """.formatted(title, views);

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

    private String createUser(String name) throws Exception {
        String body = """
        {
            "name": "%s",
            "email": "%s@mail.com"
        }
        """.formatted(name, name);

        String response = mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        return objectMapper.readTree(response).get("id").asText();
    }


    @Test
    void search_shouldReturnOnlyMatchingMovies() throws Exception {
        createMovie("Jaws", 0);
        createMovie("Funny Movie", 0);
        createMovie("Another Film", 0);

        mockMvc.perform(get("/api/movies/search?query=jaws"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(1)) 
            .andExpect(jsonPath("$[0].title").value("Jaws"));
    }

    @Test
    void search_shouldBeCaseInsensitive() throws Exception {
        createMovie("Jaws", 0);
        createMovie("Another Movie", 0);

        mockMvc.perform(get("/api/movies/search?query=JAWS"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(1))
            .andExpect(jsonPath("$[0].title").value("Jaws"));
    }

    @Test
    void search_shouldMatchPartialWord() throws Exception {
        createMovie("Romanian Horror", 0);
        createMovie("Comedy Film", 0);

        mockMvc.perform(get("/api/movies/search?query=rom"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(1))
            .andExpect(jsonPath("$[0].title").value("Romanian Horror"));
    }

    @Test
    void search_shouldMatchMultipleWords() throws Exception {
        createMovie("Jim Carrey Comedy", 0);
        createMovie("Random Movie", 0);

        mockMvc.perform(get("/api/movies/search?query=jim carrey"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(1))
            .andExpect(jsonPath("$[0].title").value("Jim Carrey Comedy"));
    }

    @Test
    void getMoviesSortedByViews_shouldReturnMovies() throws Exception {
        createMovie("Low", 0);
        createMovie("High", 0);

        mockMvc.perform(get("/api/movies/sorted/views"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void getMoviesSortedByRating_shouldReturnSorted() throws Exception {
        String movie1 = createMovie("Bad", 0);
        String movie2 = createMovie("Good", 0);

        String user = createUser("user");

        createRating(user, movie1, 2);
        createRating(user, movie2, 5);

        mockMvc.perform(get("/api/movies/sorted/rating"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].title").value("Good"))
            .andExpect(jsonPath("$[1].title").value("Bad"));
    }
}
