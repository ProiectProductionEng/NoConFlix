package ro.unibuc.prodeng.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import ro.unibuc.prodeng.model.ReviewEntity;
import ro.unibuc.prodeng.repository.ReviewRepository;
import ro.unibuc.prodeng.request.CreateReviewRequest;
import ro.unibuc.prodeng.request.UpdateReviewRequest;
import ro.unibuc.prodeng.response.ReviewResponse;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private ReviewService reviewService;


    @Test
    void addReview_shouldSave_whenValid() throws Exception {
        // Arrange
        CreateReviewRequest request = new CreateReviewRequest("u1", "m1", "nice");

        when(userService.getUserEntityById(anyString())).thenReturn(null);
        when(reviewRepository.findByUserIdAndMovieId(anyString(), anyString()))
                .thenReturn(Optional.empty());

        ReviewEntity saved = new ReviewEntity();
        saved.setId("r1");
        saved.setUserId("u1");
        saved.setMovieId("m1");
        saved.setComment("nice");

        when(reviewRepository.save(any())).thenReturn(saved);

        // Act
        ReviewResponse response = reviewService.addReview(request);

        // Assert
        assertEquals("r1", response.id());
        assertEquals("nice", response.comment());
    }

    @Test
    void addReview_shouldThrow_whenAlreadyExists() {
        // Arrange
        CreateReviewRequest request = new CreateReviewRequest("u1", "m1", "nice");

        when(userService.getUserEntityById(anyString())).thenReturn(null);
        when(reviewRepository.findByUserIdAndMovieId(anyString(), anyString()))
                .thenReturn(Optional.of(new ReviewEntity()));

        // Act & Assert
        assertThrows(IllegalArgumentException.class,() -> reviewService.addReview(request));
    }


    @Test
    void deleteReview_shouldDelete_whenUserIsOwner() throws Exception {
        // Arrange
        ReviewEntity review = new ReviewEntity();
        review.setId("r1");
        review.setUserId("u1");

        when(reviewRepository.findById("r1")).thenReturn(Optional.of(review));
        when(userService.getUserEntityById(anyString())).thenReturn(null);

        // Act
        reviewService.deleteReview("r1", "u1");

        // Assert
        verify(reviewRepository).delete(review);
    }


    @Test
    void deleteReview_shouldThrow_whenNotOwner() throws Exception {
        // Arrange
        ReviewEntity review = new ReviewEntity();
        review.setId("r1");
        review.setUserId("u1");

        when(reviewRepository.findById("r1")).thenReturn(Optional.of(review));
        when(userService.getUserEntityById(anyString())).thenReturn(null);

        // Act & Assert
        assertThrows(IllegalArgumentException.class,() -> reviewService.deleteReview("r1", "u2"));
    }


    @Test
    void updateReview_shouldUpdate_whenUserIsOwner() throws Exception {
        // Arrange
        ReviewEntity review = new ReviewEntity();
        review.setId("r1");
        review.setUserId("u1");

        UpdateReviewRequest request = new UpdateReviewRequest("u1", "updated");

        when(reviewRepository.findById("r1")).thenReturn(Optional.of(review));
        when(userService.getUserEntityById(anyString())).thenReturn(null);
        when(reviewRepository.save(any())).thenReturn(review);

        // Act
        ReviewResponse response = reviewService.updateReview("r1", request);

        // Assert
        assertEquals("updated", response.comment());
    }


    @Test
    void updateReview_shouldThrow_whenNotOwner() throws Exception {
        // Arrange
        ReviewEntity review = new ReviewEntity();
        review.setId("r1");
        review.setUserId("u1");

        UpdateReviewRequest request = new UpdateReviewRequest("u2", "updated");

        when(reviewRepository.findById("r1")).thenReturn(Optional.of(review));
        when(userService.getUserEntityById(anyString())).thenReturn(null);

        // Act & Assert
        assertThrows(IllegalArgumentException.class,() -> reviewService.updateReview("r1", request));
    }



    @Test
    void getReviewsForMovie_shouldReturnMappedList() {
        // Arrange
        ReviewEntity r1 = new ReviewEntity();
        r1.setId("r1");
        r1.setMovieId("m1");
        r1.setUserId("u1");
        r1.setComment("nice");

        ReviewEntity r2 = new ReviewEntity();
        r2.setId("r2");
        r2.setMovieId("m1");
        r2.setUserId("u2");
        r2.setComment("ok");

        when(reviewRepository.findByMovieId("m1")).thenReturn(List.of(r1, r2));

        // Act
        List<ReviewResponse> result = reviewService.getReviewsForMovie("m1");

        // Assert
        assertEquals(2, result.size());
        assertEquals("nice", result.get(0).comment());
    }


    @Test
    void getReviewsForMovie_shouldReturnEmptyList_whenNoReviews() {
        // Arrange
        when(reviewRepository.findByMovieId("m1")).thenReturn(List.of());

        // Act
        List<ReviewResponse> result = reviewService.getReviewsForMovie("m1");

        // Assert
        assertEquals(0, result.size());
    }
}
