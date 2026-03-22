package ro.unibuc.prodeng.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import ro.unibuc.prodeng.model.AvailabilityEntity;
import ro.unibuc.prodeng.repository.AvailabilityRepository;
import ro.unibuc.prodeng.request.CreateAvailabilityRequest;
import ro.unibuc.prodeng.request.EditAvailabilityRequest;
import ro.unibuc.prodeng.response.AvailabilityResponse;
import ro.unibuc.prodeng.exception.EntityNotFoundException;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
class AvailabilityServiceTest {

    @Mock
    private AvailabilityRepository availabilityRepository;

    @InjectMocks
    private AvailabilityService availabilityService;

    private AvailabilityEntity testAvailability1 = new AvailabilityEntity("availability-1","movie-1","subscription-1","2026-03-22");
    private AvailabilityEntity testAvailability2 = new AvailabilityEntity("availability-2","movie-2","subscription-2","2026-03-20");
    private CreateAvailabilityRequest createAvailabilityRequest = new CreateAvailabilityRequest("movie-3","subscription-3","2030-03-20");
    private EditAvailabilityRequest editAvailabilityRequest = new EditAvailabilityRequest("movie-4","subscription-4","2030-03-20");

    @Test
    void testGetAllAvailabilities_withMultipleAvailabilities_returnsAllAvailabilities() {
        // Arrange
        List<AvailabilityEntity> availabilities = Arrays.asList(
                testAvailability1,
                testAvailability2
        );
        when(availabilityRepository.findAll()).thenReturn(availabilities);

        // Act
        List<AvailabilityResponse> result = availabilityService.getAllAvailabilities();

        // Assert
        assertEquals(2, result.size());
        assertEquals("availability-1", result.get(0).id());
        assertEquals("availability-2", result.get(1).id());
    }

    @Test
    void testGetAvailabilityById_existingAvailabilityRequested_returnsAvailability() throws EntityNotFoundException {
        // Arrange
        when(availabilityRepository.findById("availability-1")).thenReturn(Optional.of(testAvailability1));

        // Act
        AvailabilityResponse result = availabilityService.getAvailabilityById("availability-1");

        // Assert
        assertNotNull(result);
        assertEquals("2026-03-22", result.availableUntil_date());
    }

    @Test
    void testGetAvailabilityEntityById_existingAvailabilityRequested_returnsAvailability() throws EntityNotFoundException {
        // Arrange
        when(availabilityRepository.findById("availability-1")).thenReturn(Optional.of(testAvailability1));

        // Act
        AvailabilityEntity result = availabilityService.getAvailabilityEntityById("availability-1");

        // Assert
        assertNotNull(result);
        assertEquals(result,testAvailability1);
    }
    
    @Test
    void testGetAvailabilityById_nonExistingAvailabilityRequested_throwsEntityNotFoundException() {
        // Arrange
        when(availabilityRepository.findById("availability-999")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> availabilityService.getAvailabilityById("availability-999"));
    }

    @Test
    void testCreateAvailability_newAvailabilityWithValidData_createsAndReturnsAvailability() {
        // Arrange
        when(availabilityRepository.save(any(AvailabilityEntity.class))).thenAnswer(invocation -> {
            AvailabilityEntity entity = invocation.getArgument(0);
            // Simulate MongoDB generating an ID for new entities
            String id = "availability-123";
            return new AvailabilityEntity(id, "movie-3","subscription-3","2030-03-20");
        });

        // Act
        AvailabilityResponse result = availabilityService.createAvailability(createAvailabilityRequest);

        // Assert
        assertNotNull(result);
        assertNotNull(result.id());
        assertEquals("movie-3", result.movieId());
        verify(availabilityRepository, times(1)).save(any(AvailabilityEntity.class));
    }

    @Test
    void testEditAvailability_existingAvailabilityRequested_editsAvailabilitySuccessfully() throws EntityNotFoundException {
        // Arrange
        when(availabilityRepository.findById("availability-1")).thenReturn(Optional.of(testAvailability1));
        when(availabilityRepository.save(any(AvailabilityEntity.class))).thenAnswer(invocation -> {
            AvailabilityEntity entity = invocation.getArgument(0);
            // Simulate MongoDB generating an ID for new entities
            String id = entity.id() == null ? "generated-id-123" : entity.id();
            return new AvailabilityEntity(
                id,
                entity.movieId(),
                entity.subscriptionId(),
                entity.availableUntil_date()
            );
        });

        // Act
        AvailabilityResponse result = availabilityService.editAvailability("availability-1",editAvailabilityRequest);

        // Assert
        assertNotNull(result);
        assertEquals("availability-1", result.id());
        assertEquals("movie-4", result.movieId());
    }

    @Test
    void testEditAvailability_nonExistingAvailabilityRequested_throwsEntityNotFoundException() {
        // Arrange
        when(availabilityRepository.findById("availability-999")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> availabilityService.editAvailability("availability-999", editAvailabilityRequest));
    }

    @Test
    void testDeleteAvailability_existingAvailabilityRequested_deletesSuccessfully() throws EntityNotFoundException {
        // Arrange
        when(availabilityRepository.existsById("availability-1")).thenReturn(true);

        // Act
        availabilityService.deleteAvailability("availability-1");

        // Assert
        verify(availabilityRepository, times(1)).deleteById("availability-1");
    }

    @Test
    void testDeleteAvailability_nonExistingAvailabilityRequested_throwsEntityNotFoundException() {
        // Arrange
        when(availabilityRepository.existsById("availability-999")).thenReturn(false);

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> availabilityService.deleteAvailability("availability-999"));
    }
}
