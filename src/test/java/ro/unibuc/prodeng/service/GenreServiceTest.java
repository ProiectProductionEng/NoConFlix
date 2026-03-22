package ro.unibuc.prodeng.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import ro.unibuc.prodeng.model.GenreEntity;
import ro.unibuc.prodeng.repository.GenreRepository;
import ro.unibuc.prodeng.request.CreateGenreRequest;
import ro.unibuc.prodeng.request.EditGenreRequest;
import ro.unibuc.prodeng.response.GenreResponse;
import ro.unibuc.prodeng.exception.EntityNotFoundException;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
class GenreServiceTest {

    @Mock
    private GenreRepository genreRepository;

    @InjectMocks
    private GenreService genreService;

    private GenreEntity testGenre1 = new GenreEntity("genre-1","Horror");
    private GenreEntity testGenre2 = new GenreEntity("genre-2","Comedy");
    private CreateGenreRequest createGenreRequest = new CreateGenreRequest("Family");
    private EditGenreRequest editGenreRequest = new EditGenreRequest("Thriller");

    @Test
    void testGetAllGenres_withMultipleGenres_returnsAllGenres() {
        // Arrange
        List<GenreEntity> genres = Arrays.asList(
                testGenre1,
                testGenre2
        );
        when(genreRepository.findAll()).thenReturn(genres);

        // Act
        List<GenreResponse> result = genreService.getAllGenres();

        // Assert
        assertEquals(2, result.size());
        assertEquals("Horror", result.get(0).name());
        assertEquals("Comedy", result.get(1).name());
    }

    @Test
    void testGetGenreById_existingGenreRequested_returnsGenre() throws EntityNotFoundException {
        // Arrange
        when(genreRepository.findById("genre-1")).thenReturn(Optional.of(testGenre1));

        // Act
        GenreResponse result = genreService.getGenreById("genre-1");

        // Assert
        assertNotNull(result);
        assertEquals("Horror", result.name());
    }

    @Test
    void testGetGenreEntityById_existingGenreRequested_returnsGenre() throws EntityNotFoundException {
        // Arrange
        when(genreRepository.findById("genre-1")).thenReturn(Optional.of(testGenre1));

        // Act
        GenreEntity result = genreService.getGenreEntityById("genre-1");

        // Assert
        assertNotNull(result);
        assertEquals(result,testGenre1);
    }
    
    @Test
    void testGetGenreById_nonExistingGenreRequested_throwsEntityNotFoundException() {
        // Arrange
        when(genreRepository.findById("genre-999")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> genreService.getGenreById("genre-999"));
    }

    @Test
    void testCreateGenre_newGenreWithValidData_createsAndReturnsGenre() {
        // Arrange
        when(genreRepository.save(any(GenreEntity.class))).thenAnswer(invocation -> {
            GenreEntity entity = invocation.getArgument(0);
            // Simulate MongoDB generating an ID for new entities
            String id = "genre-123";
            return new GenreEntity(id, "Family");
        });

        // Act
        GenreResponse result = genreService.createGenre(createGenreRequest);

        // Assert
        assertNotNull(result);
        assertNotNull(result.id());
        assertEquals("Family", result.name());
        verify(genreRepository, times(1)).save(any(GenreEntity.class));
    }

    @Test
    void testEditGenre_existingGenreRequested_editsGenreSuccessfully() throws EntityNotFoundException {
        // Arrange
        when(genreRepository.findById("genre-1")).thenReturn(Optional.of(testGenre1));
        when(genreRepository.save(any(GenreEntity.class))).thenAnswer(invocation -> {
            GenreEntity entity = invocation.getArgument(0);
            // Simulate MongoDB generating an ID for new entities
            String id = entity.id() == null ? "generated-id-123" : entity.id();
            return new GenreEntity(
                id,
                entity.name()
            );
        });

        // Act
        GenreResponse result = genreService.editGenre("genre-1",editGenreRequest);

        // Assert
        assertNotNull(result);
        assertEquals("genre-1", result.id());
        assertEquals("Thriller", result.name());
    }

    @Test
    void testEditGenre_nonExistingGenreRequested_throwsEntityNotFoundException() {
        // Arrange
        when(genreRepository.findById("genre-999")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> genreService.editGenre("genre-999", editGenreRequest));
    }

    @Test
    void testDeleteGenre_existingGenreRequested_deletesSuccessfully() throws EntityNotFoundException {
        // Arrange
        when(genreRepository.existsById("genre-1")).thenReturn(true);

        // Act
        genreService.deleteGenre("genre-1");

        // Assert
        verify(genreRepository, times(1)).deleteById("genre-1");
    }

    @Test
    void testDeleteGenre_nonExistingGenreRequested_throwsEntityNotFoundException() {
        // Arrange
        when(genreRepository.existsById("genre-999")).thenReturn(false);

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> genreService.deleteGenre("genre-999"));
    }
}
