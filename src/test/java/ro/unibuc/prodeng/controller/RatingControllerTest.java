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

import ro.unibuc.prodeng.request.CreateRatingRequest;
import ro.unibuc.prodeng.request.UpdateRatingRequest;
import ro.unibuc.prodeng.response.RatingResponse;
import ro.unibuc.prodeng.service.RatingService;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
class RatingControllerTest {

    @Mock
    private RatingService ratingService;

    @InjectMocks
    private RatingController ratingController;

    private MockMvc mockMvc;

    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach  //se executa inainte de fiecare test
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(ratingController).build();  //simuleaza un server fake
    }

    @Test
    void addRating_validRequest_returnsCreated() throws Exception {
        // Arrange
        CreateRatingRequest request = new CreateRatingRequest("u1", "m1", 5);  //body-ul requestului
        RatingResponse response = new RatingResponse("r1","m1","u1",5); //ceea ce vreau sa retunreze service-ul

        when(ratingService.addRating(any())).thenReturn(response); //apelez direct response

        // Act & Assert
        mockMvc.perform(post("/api/ratings")  //simulez requestul http
                .contentType(MediaType.APPLICATION_JSON)  //header
                .content(objectMapper.writeValueAsString(request)))  //transform obiectul in json
                .andExpect(status().isCreated())  //verific ca status code e 201
                .andExpect(jsonPath("$.id").value("r1"))  //verific json response 
                .andExpect(jsonPath("$.value").value(5));

        verify(ratingService, times(1)).addRating(any());  //verific daca controllerul a apelat service-ul exact o data
    }


    @Test
    void getRatings_existingMovie_returnsList() throws Exception {
        // Arrange
        List<RatingResponse> ratings = List.of(
                new RatingResponse("r1","m1","u1",5),
                new RatingResponse("r2","m1","u2",3)
        );

        when(ratingService.getRatingsForMovie("m1")).thenReturn(ratings);

        // Act & Assert
        mockMvc.perform(get("/api/ratings/movie/m1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));

        verify(ratingService).getRatingsForMovie("m1");
    }


    @Test
    void updateRating_validRequest_returnsUpdated() throws Exception {
        // Arrange
        UpdateRatingRequest request = new UpdateRatingRequest("u1", 4);
        RatingResponse response = new RatingResponse("r1","m1","u1",4);

        when(ratingService.updateRating(eq("r1"), any())).thenReturn(response);

        // Act & Assert
        mockMvc.perform(put("/api/ratings/r1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.value").value(4));

        verify(ratingService).updateRating(eq("r1"), any());
    }


    @Test
    void deleteRating_validRequest_returnsNoContent() throws Exception {
        // Act & Assert
        mockMvc.perform(delete("/api/ratings/r1")
                .param("userId", "u1"))
                .andExpect(status().isNoContent());

        verify(ratingService).deleteRating("r1", "u1");
    }
}