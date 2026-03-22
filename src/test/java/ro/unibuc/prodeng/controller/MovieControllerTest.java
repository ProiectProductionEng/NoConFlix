package ro.unibuc.prodeng.controller;

import com.fasterxml.jackson.databind.ObjectMapper;

import ro.unibuc.prodeng.exception.EntityNotFoundException;
import ro.unibuc.prodeng.request.CreateMovieRequest;
import ro.unibuc.prodeng.request.EditMovieRequest;
import ro.unibuc.prodeng.response.MovieResponse;
import ro.unibuc.prodeng.response.MovieResponse;
import ro.unibuc.prodeng.service.MovieService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

@ExtendWith(SpringExtension.class)
class MovieControllerTest {

   @Mock
   private MovieService movieService;

   @InjectMocks
   private MovieController movieController;

   private MockMvc mockMvc;

   private ObjectMapper objectMapper = new ObjectMapper();

   private MovieResponse testMovie1 = new MovieResponse("movie-1","The Great Gatsby","the best movie ever","genre-commedy","2026-03-21",3000,5542543,"google.com","google.com");
   private MovieResponse testMovie2 = new MovieResponse("movie-2","Spiderman: No way home","newest movie","genre-horror","2026-03-22",3000,5542543,"google.com","google.com");
   private CreateMovieRequest createMovieRequest = new CreateMovieRequest("The conjuring","great","genre-thriller","1996-03-22",100,"google.com","google.com");
   private EditMovieRequest editMovieRequest = new EditMovieRequest("The conjuring","great","genre-thriller","1996-03-22",100,500,"google.com","google.com");

   @BeforeEach
   void setUp() {
      mockMvc = MockMvcBuilders.standaloneSetup(movieController).build();
   }

    @Test
    void testCreateMovie_validRequestProvided_createsAndReturnsMovie() throws Exception {
        // Arrange
        when(movieService.createMovie(any(CreateMovieRequest.class))).thenReturn(testMovie1);
        
        // Act & Assert
        mockMvc.perform(post("/api/movies")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createMovieRequest)))
                .andExpect(status().isCreated())
                //"The Great Gatsby","the best movie ever","genre-commedy","2026-03-21",3000,5542543,"google.com","google.com"
                .andExpect(jsonPath("$.id", is("movie-1")))
                .andExpect(jsonPath("$.title", is("The Great Gatsby")))
                .andExpect(jsonPath("$.description", is("the best movie ever")))
                .andExpect(jsonPath("$.genreId", is("genre-commedy")))
                .andExpect(jsonPath("$.releaseDate", is("2026-03-21")))
                .andExpect(jsonPath("$.duration", is(3000)))
                .andExpect(jsonPath("$.totalViews", is(5542543)))
                .andExpect(jsonPath("$.thumbnailUrl", is("google.com")))
                .andExpect(jsonPath("$.videoUrl", is("google.com")));
        
        verify(movieService, times(1)).createMovie(any(CreateMovieRequest.class));
    }
   
    @Test
    void testUpdateMovie_existingMovieRequested_updatesAndReturnsMovie() throws Exception {
        // Arrange
        String movieId = "movie-1";
        MovieResponse updatedMovie=new MovieResponse("movie-1","The conjuring","great","genre-thriller","1996-03-22",100,500,"google.com","google.com");
        when(movieService.editMovie(eq(movieId), any(editMovieRequest.getClass()))).thenReturn(updatedMovie);

        // Act & Assert
        mockMvc.perform(put("/api/movies/{id}", movieId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(editMovieRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is("movie-1")))
                .andExpect(jsonPath("$.title", is("The conjuring")))
                .andExpect(jsonPath("$.description", is("great")))
                .andExpect(jsonPath("$.genreId", is("genre-thriller")))
                .andExpect(jsonPath("$.releaseDate", is("1996-03-22")))
                .andExpect(jsonPath("$.duration", is(100)))
                .andExpect(jsonPath("$.totalViews", is(500)))
                .andExpect(jsonPath("$.thumbnailUrl", is("google.com")))
                .andExpect(jsonPath("$.videoUrl", is("google.com")));
        
        verify(movieService, times(1)).editMovie(eq(movieId), any(editMovieRequest.getClass()));
    }

    @Test
    void testGetAllMovies_withMultipleMovies_returnsListOfMovies() throws Exception {
        // Arrange
        List<MovieResponse> movies = Arrays.asList(testMovie1, testMovie2);
        when(movieService.getAllMovies()).thenReturn(movies);
        
        // Act & Assert
        mockMvc.perform(get("/api/movies")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is("movie-1")))
                .andExpect(jsonPath("$[0].title", is("The Great Gatsby")))
                .andExpect(jsonPath("$[0].description", is("the best movie ever")))
                .andExpect(jsonPath("$[0].genreId", is("genre-commedy")))
                .andExpect(jsonPath("$[0].releaseDate", is("2026-03-21")))
                .andExpect(jsonPath("$[0].duration", is(3000)))
                .andExpect(jsonPath("$[0].totalViews", is(5542543)))
                .andExpect(jsonPath("$[0].thumbnailUrl", is("google.com")))
                .andExpect(jsonPath("$[0].videoUrl", is("google.com")))

                .andExpect(jsonPath("$[1].id", is("movie-2")))
                .andExpect(jsonPath("$[1].title", is("Spiderman: No way home")))
                .andExpect(jsonPath("$[1].description", is("newest movie")))
                .andExpect(jsonPath("$[1].genreId", is("genre-horror")))
                .andExpect(jsonPath("$[1].releaseDate", is("2026-03-22")))
                .andExpect(jsonPath("$[1].duration", is(3000)))
                .andExpect(jsonPath("$[1].totalViews", is(5542543)))
                .andExpect(jsonPath("$[1].thumbnailUrl", is("google.com")))
                .andExpect(jsonPath("$[1].videoUrl", is("google.com")));
        
        verify(movieService, times(1)).getAllMovies();
    }
    
    @Test
    void testDeleteMovie_ExistingMovieRequested_deletesMovie() throws Exception {
        String movieId = "movie-1";
        doNothing().when(movieService).deleteMovie(movieId);

        mockMvc.perform(delete("/api/movies/{id}", movieId))
                .andExpect(status().isNoContent());

        verify(movieService, times(1)).deleteMovie(movieId);
    }
   }
   
   // Further tests go here