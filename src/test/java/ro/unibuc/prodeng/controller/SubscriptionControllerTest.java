package ro.unibuc.prodeng.controller;

import com.fasterxml.jackson.databind.ObjectMapper;

import ro.unibuc.prodeng.exception.EntityNotFoundException;
import ro.unibuc.prodeng.model.SubscriptionEntity;
import ro.unibuc.prodeng.request.CreateSubscriptionRequest;
import ro.unibuc.prodeng.request.EditSubscriptionRequest;
import ro.unibuc.prodeng.response.SubscriptionResponse;
import ro.unibuc.prodeng.response.SubscriptionResponse;
import ro.unibuc.prodeng.service.SubscriptionService;

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
class SubscriptionControllerTest {

   @Mock
   private SubscriptionService subscriptionService;

   @InjectMocks
   private SubscriptionController subscriptionController;

   private MockMvc mockMvc;

   private ObjectMapper objectMapper = new ObjectMapper();

    private SubscriptionResponse testSubscription1 = new SubscriptionResponse("subscription-1","user-1","10 day trial",0.0f,10,"2026-03-21");
    private SubscriptionResponse testSubscription2 = new SubscriptionResponse("subscription-2","user-2","Basic Plan",10.0f,365,"2026-03-22");
    private CreateSubscriptionRequest createSubscriptionRequest = new CreateSubscriptionRequest("user-3","Premium Plan",5.0f,365,"2026-03-22");
    private EditSubscriptionRequest editSubscriptionRequest = new EditSubscriptionRequest("user-1","30 day trial",0.0f,30,"2026-03-22");

   @BeforeEach
   void setUp() {
      mockMvc = MockMvcBuilders.standaloneSetup(subscriptionController).build();
   }

    @Test
    void testCreateSubscription_validRequestProvided_createsAndReturnsSubscription() throws Exception {
        // Arrange
        when(subscriptionService.createSubscription(any(CreateSubscriptionRequest.class))).thenReturn(testSubscription1);
        
        // Act & Assert
        mockMvc.perform(post("/api/subscriptions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createSubscriptionRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is("subscription-1")))
                .andExpect(jsonPath("$.userId", is("user-1")))
                .andExpect(jsonPath("$.name", is("10 day trial")))
                .andExpect(jsonPath("$.price", is(0.0)))
                .andExpect(jsonPath("$.duration", is(10)))
                .andExpect(jsonPath("$.end_date", is("2026-03-21")));
        
        verify(subscriptionService, times(1)).createSubscription(any(CreateSubscriptionRequest.class));
    }
   
    @Test
    void testUpdateSubscription_existingSubscriptionRequested_updatesAndReturnsSubscription() throws Exception {
        // Arrange
        String subscriptionId = "subscription-1";
        SubscriptionResponse updatedSubscription=new SubscriptionResponse("subscription-1","user-1","30 day trial",0.0f,30,"2026-03-22");
        when(subscriptionService.editSubscription(eq(subscriptionId), any(editSubscriptionRequest.getClass()))).thenReturn(updatedSubscription);

        // Act & Assert
        mockMvc.perform(put("/api/subscriptions/{id}", subscriptionId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(editSubscriptionRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is("subscription-1")))
                .andExpect(jsonPath("$.userId", is("user-1")))
                .andExpect(jsonPath("$.name", is("30 day trial")))
                .andExpect(jsonPath("$.price", is(0.0)))
                .andExpect(jsonPath("$.duration", is(30)))
                .andExpect(jsonPath("$.end_date", is("2026-03-22")));
        
        verify(subscriptionService, times(1)).editSubscription(eq(subscriptionId), any(editSubscriptionRequest.getClass()));
    }

    @Test
    void testGetAllSubscriptions_withMultipleSubscriptions_returnsListOfSubscriptions() throws Exception {
        // Arrange
        List<SubscriptionResponse> subscriptions = Arrays.asList(testSubscription1, testSubscription2);
        when(subscriptionService.getAllSubscriptions()).thenReturn(subscriptions);
        
        // Act & Assert
        mockMvc.perform(get("/api/subscriptions")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is("subscription-1")))
                .andExpect(jsonPath("$[0].userId", is("user-1")))
                .andExpect(jsonPath("$[0].name", is("10 day trial")))
                .andExpect(jsonPath("$[0].price", is(0.0)))
                .andExpect(jsonPath("$[0].duration", is(10)))
                .andExpect(jsonPath("$[0].end_date", is("2026-03-21")))

                .andExpect(jsonPath("$[1].id", is("subscription-2")))
                .andExpect(jsonPath("$[1].userId", is("user-2")))
                .andExpect(jsonPath("$[1].name", is("Basic Plan")))
                .andExpect(jsonPath("$[1].price", is(10.0)))
                .andExpect(jsonPath("$[1].duration", is(365)))
                .andExpect(jsonPath("$[1].end_date", is("2026-03-22")));
        
        verify(subscriptionService, times(1)).getAllSubscriptions();
    }
    
    @Test
    void testDeleteSubscription_ExistingSubscriptionRequested_deletesSubscription() throws Exception {
        String subscriptionId = "subscription-1";
        doNothing().when(subscriptionService).deleteSubscription(subscriptionId);

        mockMvc.perform(delete("/api/subscriptions/{id}", subscriptionId))
                .andExpect(status().isNoContent());

        verify(subscriptionService, times(1)).deleteSubscription(subscriptionId);
    }
   }
   
   // Further tests go here