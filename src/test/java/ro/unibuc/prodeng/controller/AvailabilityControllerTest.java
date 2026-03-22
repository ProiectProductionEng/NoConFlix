package ro.unibuc.prodeng.controller;

import com.fasterxml.jackson.databind.ObjectMapper;

import ro.unibuc.prodeng.exception.EntityNotFoundException;
import ro.unibuc.prodeng.model.AvailabilityEntity;
import ro.unibuc.prodeng.request.CreateAvailabilityRequest;
import ro.unibuc.prodeng.request.EditAvailabilityRequest;
import ro.unibuc.prodeng.response.AvailabilityResponse;
import ro.unibuc.prodeng.response.AvailabilityResponse;
import ro.unibuc.prodeng.service.AvailabilityService;

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
class AvailabilityControllerTest {

   @Mock
   private AvailabilityService availabilityService;

   @InjectMocks
   private AvailabilityController availabilityController;

   private MockMvc mockMvc;

   private ObjectMapper objectMapper = new ObjectMapper();

    private AvailabilityResponse testAvailability1 = new AvailabilityResponse("availability-1","movie-1","subscription-1","2026-03-22");
    private AvailabilityResponse testAvailability2 = new AvailabilityResponse("availability-2","movie-2","subscription-2","2026-03-20");
    private CreateAvailabilityRequest createAvailabilityRequest = new CreateAvailabilityRequest("movie-3","subscription-3","2030-03-20");
    private EditAvailabilityRequest editAvailabilityRequest = new EditAvailabilityRequest("movie-4","subscription-4","2030-03-20");

   @BeforeEach
   void setUp() {
      mockMvc = MockMvcBuilders.standaloneSetup(availabilityController).build();
   }

    @Test
    void testCreateAvailability_validRequestProvided_createsAndReturnsAvailability() throws Exception {
        // Arrange
        when(availabilityService.createAvailability(any(CreateAvailabilityRequest.class))).thenReturn(testAvailability1);
        
        // Act & Assert
        mockMvc.perform(post("/api/availabilities")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createAvailabilityRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is("availability-1")))
                .andExpect(jsonPath("$.movieId", is("movie-1")));
        
        verify(availabilityService, times(1)).createAvailability(any(CreateAvailabilityRequest.class));
    }
   
    @Test
    void testUpdateAvailability_existingAvailabilityRequested_updatesAndReturnsAvailability() throws Exception {
        // Arrange
        String availabilityId = "availability-1";
        AvailabilityResponse updatedAvailability=new AvailabilityResponse("availability-1","movie-4","subscription-4","2030-03-20");
        when(availabilityService.editAvailability(eq(availabilityId), any(editAvailabilityRequest.getClass()))).thenReturn(updatedAvailability);

        // Act & Assert
        mockMvc.perform(put("/api/availabilities/{id}", availabilityId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(editAvailabilityRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is("availability-1")))
                .andExpect(jsonPath("$.movieId", is("movie-4")));
        
        verify(availabilityService, times(1)).editAvailability(eq(availabilityId), any(editAvailabilityRequest.getClass()));
    }

    @Test
    void testGetAllAvailabilitys_withMultipleAvailabilitys_returnsListOfAvailabilitys() throws Exception {
        // Arrange
        List<AvailabilityResponse> availabilities = Arrays.asList(testAvailability1, testAvailability2);
        when(availabilityService.getAllAvailabilities()).thenReturn(availabilities);
        
        // Act & Assert
        mockMvc.perform(get("/api/availabilities")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is("availability-1")))
                .andExpect(jsonPath("$[0].movieId", is("movie-1")))

                .andExpect(jsonPath("$[1].id", is("availability-2")))
                .andExpect(jsonPath("$[1].movieId", is("movie-2")));
        
        verify(availabilityService, times(1)).getAllAvailabilities();
    }
    @Test
    void testDeleteAvailability_ExistingAvailabilityRequested_deletesAvailability() throws Exception {
        String availabilityId = "availability-1";
        doNothing().when(availabilityService).deleteAvailability(availabilityId);

        mockMvc.perform(delete("/api/availabilities/{id}", availabilityId))
                .andExpect(status().isNoContent());

        verify(availabilityService, times(1)).deleteAvailability(availabilityId);
    }
   }
   // Further tests go here