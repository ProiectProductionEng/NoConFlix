package ro.unibuc.prodeng.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import ro.unibuc.prodeng.model.SubscriptionEntity;
import ro.unibuc.prodeng.repository.SubscriptionRepository;
import ro.unibuc.prodeng.request.CreateSubscriptionRequest;
import ro.unibuc.prodeng.request.EditSubscriptionRequest;
import ro.unibuc.prodeng.response.SubscriptionResponse;
import ro.unibuc.prodeng.exception.EntityNotFoundException;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
class SubscriptionServiceTest {

    @Mock
    private SubscriptionRepository subscriptionRepository;

    @InjectMocks
    private SubscriptionService subscriptionService;

    private SubscriptionEntity testSubscription1 = new SubscriptionEntity("subscription-1","user-1","10 day trial",0.0f,10,"2026-03-21");
    private SubscriptionEntity testSubscription2 = new SubscriptionEntity("subscription-2","user-2","Basic Plan",10.0f,365,"2026-03-22");
    private CreateSubscriptionRequest createSubscriptionRequest = new CreateSubscriptionRequest("user-3","Premium Plan",5.0f,365,"2026-03-22");
    private EditSubscriptionRequest editSubscriptionRequest = new EditSubscriptionRequest("user-1","30 day trial",0.0f,30,"2026-03-22");

    @Test
    void testGetAllSubscriptions_withMultipleSubscriptions_returnsAllSubscriptions() {
        // Arrange
        List<SubscriptionEntity> subscriptions = Arrays.asList(
                testSubscription1,
                testSubscription2
        );
        when(subscriptionRepository.findAll()).thenReturn(subscriptions);

        // Act
        List<SubscriptionResponse> result = subscriptionService.getAllSubscriptions();

        // Assert
        assertEquals(2, result.size());
        assertEquals("10 day trial", result.get(0).name());
        assertEquals("Basic Plan", result.get(1).name());
    }

    @Test
    void testGetSubscriptionById_existingSubscriptionRequested_returnsSubscription() throws EntityNotFoundException {
        // Arrange
        when(subscriptionRepository.findById("subscription-1")).thenReturn(Optional.of(testSubscription1));

        // Act
        SubscriptionResponse result = subscriptionService.getSubscriptionById("subscription-1");

        // Assert
        assertNotNull(result);
        assertEquals("10 day trial", result.name());
        assertEquals(0.0f, result.price());
    }

    @Test
    void testGetSubscriptionEntityById_existingSubscriptionRequested_returnsSubscription() throws EntityNotFoundException {
        // Arrange
        when(subscriptionRepository.findById("subscription-1")).thenReturn(Optional.of(testSubscription1));

        // Act
        SubscriptionEntity result = subscriptionService.getSubscriptionEntityById("subscription-1");

        // Assert
        assertNotNull(result);
        assertEquals(result,testSubscription1);
    }
    
    @Test
    void testGetSubscriptionById_nonExistingSubscriptionRequested_throwsEntityNotFoundException() {
        // Arrange
        when(subscriptionRepository.findById("subscription-999")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> subscriptionService.getSubscriptionById("subscription-999"));
    }

    @Test
    void testCreateSubscription_newSubscriptionWithValidData_createsAndReturnsSubscription() {
        // Arrange
        when(subscriptionRepository.save(any(SubscriptionEntity.class))).thenAnswer(invocation -> {
            SubscriptionEntity entity = invocation.getArgument(0);
            // Simulate MongoDB generating an ID for new entities
            String id = "subscription-123";
            return new SubscriptionEntity(id, "user-3","Premium Plan",5.0f,365,"2026-03-22");
        });

        // Act
        SubscriptionResponse result = subscriptionService.createSubscription(createSubscriptionRequest);

        // Assert
        assertNotNull(result);
        assertNotNull(result.id());
        assertEquals("Premium Plan", result.name());
        assertEquals(5.0f, result.price());
        verify(subscriptionRepository, times(1)).save(any(SubscriptionEntity.class));
    }

    @Test
    void testEditSubscription_existingSubscriptionRequested_editsSubscriptionSuccessfully() throws EntityNotFoundException {
        // Arrange
        when(subscriptionRepository.findById("subscription-1")).thenReturn(Optional.of(testSubscription1));
        when(subscriptionRepository.save(any(SubscriptionEntity.class))).thenAnswer(invocation -> {
            SubscriptionEntity entity = invocation.getArgument(0);
            // Simulate MongoDB generating an ID for new entities
            String id = entity.id() == null ? "generated-id-123" : entity.id();
            return new SubscriptionEntity(
                id,
                entity.userId(),
                entity.name(),
                entity.price(),
                entity.duration(),
                entity.end_date()
            );
        });

        // Act
        SubscriptionResponse result = subscriptionService.editSubscription("subscription-1",editSubscriptionRequest);

        // Assert
        assertNotNull(result);
        assertEquals("subscription-1", result.id());
        assertEquals("user-1", result.userId());
        assertEquals("30 day trial", result.name());
        assertEquals(0.0f, result.price());
        assertEquals(30, result.duration());
        assertEquals("2026-03-22", result.end_date());
    }

    @Test
    void testEditSubscription_nonExistingSubscriptionRequested_throwsEntityNotFoundException() {
        // Arrange
        when(subscriptionRepository.findById("subscription-999")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> subscriptionService.editSubscription("subscription-999", editSubscriptionRequest));
    }

    @Test
    void testDeleteSubscription_existingSubscriptionRequested_deletesSuccessfully() throws EntityNotFoundException {
        // Arrange
        when(subscriptionRepository.existsById("subscription-1")).thenReturn(true);

        // Act
        subscriptionService.deleteSubscription("subscription-1");

        // Assert
        verify(subscriptionRepository, times(1)).deleteById("subscription-1");
    }

    @Test
    void testDeleteSubscription_nonExistingSubscriptionRequested_throwsEntityNotFoundException() {
        // Arrange
        when(subscriptionRepository.existsById("subscription-999")).thenReturn(false);

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> subscriptionService.deleteSubscription("subscription-999"));
    }
}
