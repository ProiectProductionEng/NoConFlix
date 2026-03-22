package ro.unibuc.prodeng.controller;

import com.fasterxml.jackson.databind.ObjectMapper;

import ro.unibuc.prodeng.exception.EntityNotFoundException;
import ro.unibuc.prodeng.model.GenreEntity;
import ro.unibuc.prodeng.request.CreateGenreRequest;
import ro.unibuc.prodeng.request.EditGenreRequest;
import ro.unibuc.prodeng.response.GenreResponse;
import ro.unibuc.prodeng.response.GenreResponse;
import ro.unibuc.prodeng.service.GenreService;

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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

@ExtendWith(SpringExtension.class)
class GenreControllerTest {

   @Mock
   private GenreService genreService;

   @InjectMocks
   private GenreController genreController;

   private MockMvc mockMvc;

   private ObjectMapper objectMapper = new ObjectMapper();

    private GenreResponse testGenre1 = new GenreResponse("genre-1","Horror");
    private GenreResponse testGenre2 = new GenreResponse("genre-2","Comedy");
    private CreateGenreRequest createGenreRequest = new CreateGenreRequest("Family");
    private EditGenreRequest editGenreRequest = new EditGenreRequest("Thriller");

   @BeforeEach
   void setUp() {
      mockMvc = MockMvcBuilders.standaloneSetup(genreController).build();
   }

    @Test
    void testCreateGenre_validRequestProvided_createsAndReturnsGenre() throws Exception {
        // Arrange
        when(genreService.createGenre(any(CreateGenreRequest.class))).thenReturn(testGenre1);
        
        // Act & Assert
        mockMvc.perform(post("/api/genres")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createGenreRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is("genre-1")))
                .andExpect(jsonPath("$.name", is("Horror")));
        
        verify(genreService, times(1)).createGenre(any(CreateGenreRequest.class));
    }
   
    @Test
    void testUpdateGenre_existingGenreRequested_updatesAndReturnsGenre() throws Exception {
        // Arrange
        String genreId = "genre-1";
        GenreResponse updatedGenre=new GenreResponse("genre-1","Horror");
        when(genreService.editGenre(eq(genreId), any(editGenreRequest.getClass()))).thenReturn(updatedGenre);

        // Act & Assert
        mockMvc.perform(put("/api/genres/{id}", genreId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(editGenreRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is("genre-1")))
                .andExpect(jsonPath("$.name", is("Horror")));
        
        verify(genreService, times(1)).editGenre(eq(genreId), any(editGenreRequest.getClass()));
    }

    @Test
    void testGetAllGenres_withMultipleGenres_returnsListOfGenres() throws Exception {
        // Arrange
        List<GenreResponse> genres = Arrays.asList(testGenre1, testGenre2);
        when(genreService.getAllGenres()).thenReturn(genres);
        
        // Act & Assert
        mockMvc.perform(get("/api/genres")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is("genre-1")))
                .andExpect(jsonPath("$[0].name", is("Horror")))

                .andExpect(jsonPath("$[1].id", is("genre-2")))
                .andExpect(jsonPath("$[1].name", is("Comedy")));
        
        verify(genreService, times(1)).getAllGenres();
    }
    @Test
    void testDeleteGenre_ExistingGenreRequested_deletesGenre() throws Exception {
        String genreId = "genre-1";
        doNothing().when(genreService).deleteGenre(genreId);

        mockMvc.perform(delete("/api/genres/{id}", genreId))
                .andExpect(status().isNoContent());

        verify(genreService, times(1)).deleteGenre(genreId);
    }
   }
   // Further tests go here