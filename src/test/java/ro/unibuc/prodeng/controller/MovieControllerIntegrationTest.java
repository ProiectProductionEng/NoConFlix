package ro.unibuc.prodeng.controller;

import com.fasterxml.jackson.databind.ObjectMapper;

import ro.unibuc.prodeng.IntegrationTestBase;
import ro.unibuc.prodeng.repository.MovieRepository;
import ro.unibuc.prodeng.request.CreateMovieRequest;
import ro.unibuc.prodeng.request.CreateMovieRequest;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("MovieController Integration Tests")
@Tag("IntegrationTest")
class MovieControllerIntegrationTest extends IntegrationTestBase {

   @Autowired
   private MockMvc mockMvc;

   @Autowired
   private MovieRepository movieRepository;

   @Autowired
   private ObjectMapper objectMapper;

   // clean database before each test
   @BeforeEach
   void cleanUp() {
      movieRepository.deleteAll();
   }
   
    private String createMovie(String title, String description, String genreId, String releaseDate, int duration, String videoUrl, String thumbnailUrl) throws Exception {
        CreateMovieRequest request = new CreateMovieRequest(title,description,genreId,releaseDate,duration,thumbnailUrl,videoUrl);
        String response = mockMvc.perform(post("/api/movies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.title").value(title))
                .andExpect(jsonPath("$.description").value(description))
                .andExpect(jsonPath("$.genreId").value(genreId))
                .andExpect(jsonPath("$.releaseDate").value(releaseDate))
                .andExpect(jsonPath("$.duration").value(duration))
                .andExpect(jsonPath("$.totalViews").exists())
                .andExpect(jsonPath("$.thumbnailUrl").value(thumbnailUrl))
                .andExpect(jsonPath("$.videoUrl").value(videoUrl))
                .andReturn().getResponse().getContentAsString();

        return objectMapper.readTree(response).get("id").asText();
    }
}