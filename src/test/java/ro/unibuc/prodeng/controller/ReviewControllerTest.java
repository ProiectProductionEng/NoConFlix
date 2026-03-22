package ro.unibuc.prodeng.controller;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import ro.unibuc.prodeng.request.CreateReviewRequest;
import ro.unibuc.prodeng.request.UpdateReviewRequest;
import ro.unibuc.prodeng.response.ReviewResponse;
import ro.unibuc.prodeng.service.ReviewService;

import java.time.Instant;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
class ReviewControllerTest {

    @Mock
    private ReviewService reviewService;

    @InjectMocks
    private ReviewController reviewController;

    private MockMvc mockMvc;

    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(reviewController).build();
    }

    @Test
    void addReview_validRequest_returnsCreated() throws Exception {
        // Arrange
        CreateReviewRequest request = new CreateReviewRequest("u1", "m1", "nice");

        ReviewResponse response = new ReviewResponse("r1", "m1", "u1", "nice", Instant.now());

        when(reviewService.addReview(any())).thenReturn(response);

        // Act & Assert
        mockMvc.perform(post("/api/reviews")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("r1"))
                .andExpect(jsonPath("$.comment").value("nice"));

        verify(reviewService).addReview(any());
    }


    @Test
    void getReviews_existingMovie_returnsList() throws Exception {
        // Arrange
        List<ReviewResponse> reviews = List.of(
                new ReviewResponse("r1","m1","u1","nice", Instant.now()),
                new ReviewResponse("r2","m1","u2","ok", Instant.now())
        );

        when(reviewService.getReviewsForMovie("m1")).thenReturn(reviews);

        // Act & Assert
        mockMvc.perform(get("/api/reviews/movie/m1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));

        verify(reviewService).getReviewsForMovie("m1");
    }


    @Test
    void updateReview_validRequest_returnsUpdated() throws Exception {
        // Arrange
        UpdateReviewRequest request = new UpdateReviewRequest("u1", "updated");

        ReviewResponse response = new ReviewResponse("r1","m1","u1","updated", Instant.now());

        when(reviewService.updateReview(eq("r1"), any())).thenReturn(response);

        // Act & Assert
        mockMvc.perform(put("/api/reviews/r1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.comment").value("updated"));

        verify(reviewService).updateReview(eq("r1"), any());
    }

    @Test
    void deleteReview_validRequest_returnsNoContent() throws Exception {
        // Act & Assert
        mockMvc.perform(delete("/api/reviews/r1")
                .param("userId", "u1"))
                .andExpect(status().isNoContent());

        verify(reviewService).deleteReview("r1", "u1");
    }
}