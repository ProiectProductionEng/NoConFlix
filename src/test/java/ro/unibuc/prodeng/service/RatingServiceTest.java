package ro.unibuc.prodeng.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.Test;

import ro.unibuc.prodeng.model.RatingEntity;
import ro.unibuc.prodeng.repository.RatingRepository;
import ro.unibuc.prodeng.request.CreateRatingRequest;
import ro.unibuc.prodeng.request.UpdateRatingRequest;
import ro.unibuc.prodeng.response.RatingResponse;

@ExtendWith(MockitoExtension.class)
public class RatingServiceTest {
    @Mock
    private RatingRepository ratingRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private RatingService ratingService;


    @Test
    void addRating_shouldSaveRating_whenValidRequest() throws Exception {
        // Arrange
        CreateRatingRequest request = new CreateRatingRequest("u1", "m1", 5); //creez input-ul pentru test: userul cu id-ul m1 ofera 5 stele filmului cu id u1

        when(userService.getUserEntityById(anyString())).thenReturn(null);  //simulez ca userul exista
        when(ratingRepository.existsByUserIdAndMovieId(anyString(), anyString())).thenReturn(false);  //simulez ca userul nu a mai dat rating acestui film

        RatingEntity saved = new RatingEntity();   //creez obiectul care ar veni din BD dupa save; simulez ce face repository-ul
        saved.setId("r1");
        saved.setUserId("u1");
        saved.setMovieId("m1");
        saved.setValue(5);

        when(ratingRepository.save(any())).thenReturn(saved);  //nu salvez nimic real

        // Act
        RatingResponse response = ratingService.addRating(request); //intru in metoda reala addRating; in addRating se folosesc regulile de mai sus

        // Assert
        assertEquals("r1", response.id());  //verific ca id-ul returnat e corect
        assertEquals(5, response.value());  //verific ca ratingul returnat e corect
    }


    @Test
    void addRating_shouldThrowException_whenUserAlreadyRatedMovie() {
        // Arrange
        CreateRatingRequest request = new CreateRatingRequest("u1", "m1", 5); //user m1 incearca sa dea rating la filmul u1

        when(userService.getUserEntityById(anyString())).thenReturn(null);  //nu ma intereseaza userul, simulez ca exista unul 
        when(ratingRepository.existsByUserIdAndMovieId(anyString(), anyString())).thenReturn(true);  //simulez ca userul a mai dat deja un rating

        // Act & Assert
        assertThrows(IllegalArgumentException.class,() -> ratingService.addRating(request));  //se verifica codul din lambda si se arunca exceptie
    }

    @Test
    void deleteRating_shouldDelete_whenUserIsOwner() throws Exception {
        // Arrange
        RatingEntity rating = new RatingEntity();
        rating.setId("r1");
        rating.setUserId("u1");

        when(ratingRepository.findById("r1")).thenReturn(Optional.of(rating));
        when(userService.getUserEntityById("u1")).thenReturn(null);

        // Act
        ratingService.deleteRating("r1", "u1");

        // Assert
        verify(ratingRepository).delete(rating);
    }


    @Test
    void deleteRating_shouldThrowException_whenUserIsNotOwner() throws Exception {
        // Arrange
        RatingEntity rating = new RatingEntity();
        rating.setId("r1");
        rating.setUserId("u1");

        when(ratingRepository.findById("r1")).thenReturn(Optional.of(rating));
        when(userService.getUserEntityById("u2")).thenReturn(null);

        // Act & Assert
        assertThrows(IllegalArgumentException.class,() -> ratingService.deleteRating("r1", "u2"));
    }


    @Test
    void updateRating_shouldUpdateValue_whenUserIsOwner() throws Exception {
        // Arrange
        RatingEntity rating = new RatingEntity();
        rating.setId("r1");
        rating.setUserId("u1");

        UpdateRatingRequest request = new UpdateRatingRequest("u1", 4);

        when(ratingRepository.findById("r1")).thenReturn(Optional.of(rating));
        when(userService.getUserEntityById("u1")).thenReturn(null);
        when(ratingRepository.save(any())).thenReturn(rating);

        // Act
        RatingResponse response = ratingService.updateRating("r1", request);

        // Assert
        assertEquals(4, response.value());
    }


    @Test
    void updateRating_shouldThrowException_whenUserIsNotOwner() throws Exception {
        // Arrange
        RatingEntity rating = new RatingEntity();
        rating.setId("r1");
        rating.setUserId("u1");

        UpdateRatingRequest request = new UpdateRatingRequest("u2", 4);

        when(ratingRepository.findById("r1")).thenReturn(Optional.of(rating));
        when(userService.getUserEntityById("u2")).thenReturn(null);

        // Act & Assert
        assertThrows(IllegalArgumentException.class,() -> ratingService.updateRating("r1", request));
    }

    @Test
    void getRatingsForMovie_shouldReturnMappedResponses() {
        // Arrange
        RatingEntity r1 = new RatingEntity();
        r1.setId("r1");
        r1.setMovieId("m1");
        r1.setUserId("u1");
        r1.setValue(5);

        RatingEntity r2 = new RatingEntity();
        r2.setId("r2");
        r2.setMovieId("m1");
        r2.setUserId("u2");
        r2.setValue(3);

        when(ratingRepository.findByMovieId("m1")).thenReturn(List.of(r1, r2));

        // Act
        List<RatingResponse> result = ratingService.getRatingsForMovie("m1");

        // Assert
        assertEquals(2, result.size());
        assertEquals("r1", result.get(0).id());
        assertEquals(5, result.get(0).value());
    }

    @Test
    void getRatingsForMovie_shouldReturnEmptyList_whenNoRatings() {
        // Arrange
        when(ratingRepository.findByMovieId("m1")).thenReturn(List.of());

        // Act
        List<RatingResponse> result = ratingService.getRatingsForMovie("m1");

        // Assert
        assertEquals(0, result.size());
}
}
    

