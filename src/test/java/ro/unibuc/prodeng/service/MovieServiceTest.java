package ro.unibuc.prodeng.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import ro.unibuc.prodeng.model.MovieEntity;
import ro.unibuc.prodeng.repository.MovieRepository;
import ro.unibuc.prodeng.request.CreateMovieRequest;
import ro.unibuc.prodeng.request.EditMovieRequest;
import ro.unibuc.prodeng.response.MovieResponse;
import ro.unibuc.prodeng.exception.EntityNotFoundException;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
class MovieServiceTest {

    @Mock
    private MovieRepository movieRepository;

    @InjectMocks
    private MovieService movieService;

    private MovieEntity testMovie1 = new MovieEntity("movie-1","The Great Gatsby","the best movie ever","genre-commedy","2026-03-21",3000,5542543,"google.com","google.com");
    private MovieEntity testMovie2 = new MovieEntity("movie-2","Spiderman: No way home","newest movie","genre-horror","2026-03-22",3000,5542543,"google.com","google.com");
    private CreateMovieRequest createMovieRequest = new CreateMovieRequest("The conjuring","great","genre-thriller","1996-03-22",100,"google.com","google.com");
    private EditMovieRequest editMovieRequest = new EditMovieRequest("The conjuring","great","genre-thriller","1996-03-22",100,500,"google.com","google.com");

    @Test
    void testGetAllMovies_withMultipleMovies_returnsAllMovies() {
        // Arrange
        List<MovieEntity> movies = Arrays.asList(
                testMovie1,
                testMovie2
        );
        when(movieRepository.findAll()).thenReturn(movies);

        // Act
        List<MovieResponse> result = movieService.getAllMovies();

        // Assert
        assertEquals(2, result.size());
        assertEquals("The Great Gatsby", result.get(0).title());
        assertEquals("Spiderman: No way home", result.get(1).title());
    }

    @Test
    void testGetMovieById_existingMovieRequested_returnsMovie() throws EntityNotFoundException {
        // Arrange
        when(movieRepository.findById("movie-1")).thenReturn(Optional.of(testMovie1));

        // Act
        MovieResponse result = movieService.getMovieById("movie-1");

        // Assert
        assertNotNull(result);
        assertEquals("The Great Gatsby", result.title());
        assertEquals("the best movie ever", result.description());
    }

    @Test
    void testGetMovieEntityById_existingMovieRequested_returnsMovie() throws EntityNotFoundException {
        // Arrange
        when(movieRepository.findById("movie-1")).thenReturn(Optional.of(testMovie1));

        // Act
        MovieEntity result = movieService.getMovieEntityById("movie-1");

        // Assert
        assertNotNull(result);
        assertEquals(result,testMovie1);
    }
    
    @Test
    void testGetMovieById_nonExistingMovieRequested_throwsEntityNotFoundException() {
        // Arrange
        when(movieRepository.findById("movie-999")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> movieService.getMovieById("movie-999"));
    }

    @Test
    void testCreateMovie_newMovieWithValidData_createsAndReturnsMovie() {
        // Arrange
        when(movieRepository.save(any(MovieEntity.class))).thenAnswer(invocation -> {
            MovieEntity entity = invocation.getArgument(0);
            // Simulate MongoDB generating an ID for new entities
            String id = "movie-123";
            return new MovieEntity(id, "The conjuring","great","genre-thriller","1996-03-22",100,0,"google.com","google.com");
        });

        // Act
        MovieResponse result = movieService.createMovie(createMovieRequest);

        // Assert
        assertNotNull(result);
        assertNotNull(result.id());
        assertEquals("The conjuring", result.title());
        assertEquals("great", result.description());
        verify(movieRepository, times(1)).save(any(MovieEntity.class));
    }

    @Test
    void testEditMovie_existingMovieRequested_editsMovieSuccessfully() throws EntityNotFoundException {
        // Arrange
        when(movieRepository.findById("movie-1")).thenReturn(Optional.of(testMovie1));
        when(movieRepository.save(any(MovieEntity.class))).thenAnswer(invocation -> {
            MovieEntity entity = invocation.getArgument(0);
            // Simulate MongoDB generating an ID for new entities
            String id = entity.id() == null ? "generated-id-123" : entity.id();
            return new MovieEntity(
                id,
                entity.title(),
                entity.description(),
                entity.genreId(),
                entity.releaseDate(),
                entity.duration(),
                entity.totalViews(),
                entity.thumbnailUrl(),
                entity.videoUrl()
            );
        });

        // Act
        MovieResponse result = movieService.editMovie("movie-1",editMovieRequest);

        // Assert
        assertNotNull(result);
        assertEquals("movie-1", result.id());
        assertEquals("The conjuring", result.title());
        assertEquals("great", result.description());
        assertEquals("genre-thriller", result.genreId());
        assertEquals("1996-03-22", result.releaseDate());
        assertEquals(100, result.duration());
        assertEquals(500, result.totalViews());
        assertEquals("google.com", result.thumbnailUrl());
        assertEquals("google.com", result.videoUrl());
    }

    @Test
    void testEditMovie_nonExistingMovieRequested_throwsEntityNotFoundException() {
        // Arrange
        when(movieRepository.findById("movie-999")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> movieService.editMovie("movie-999", editMovieRequest));
    }

    @Test
    void testDeleteMovie_existingMovieRequested_deletesSuccessfully() throws EntityNotFoundException {
        // Arrange
        when(movieRepository.existsById("movie-1")).thenReturn(true);

        // Act
        movieService.deleteMovie("movie-1");

        // Assert
        verify(movieRepository, times(1)).deleteById("movie-1");
    }

    @Test
    void testDeleteMovie_nonExistingMovieRequested_throwsEntityNotFoundException() {
        // Arrange
        when(movieRepository.existsById("movie-999")).thenReturn(false);

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> movieService.deleteMovie("movie-999"));
    }
}
