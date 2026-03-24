package ro.unibuc.prodeng.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ro.unibuc.prodeng.model.Watchlist;
import ro.unibuc.prodeng.repository.WatchlistRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
class WatchlistServiceTest {

    @Mock
    private WatchlistRepository watchlistRepository;

    @InjectMocks
    private WatchlistService watchlistService;

    @Test
    void testAddToWatchlist_whenMovieDoesNotExist_createsAndReturnsWatchlist() {
        // Arrange
        String userId = "user-1";
        String movieId = "movie-1";
        Watchlist savedWatchlist = new Watchlist(userId, movieId);

        when(watchlistRepository.existsByUserIdAndMovieId(userId, movieId)).thenReturn(false);
        when(watchlistRepository.save(any(Watchlist.class))).thenReturn(savedWatchlist);

        // Act
        Watchlist result = watchlistService.addToWatchlist(userId, movieId);

        // Assert
        assertNotNull(result);
        assertEquals(userId, result.getUserId());
        assertEquals(movieId, result.getMovieId());
        verify(watchlistRepository, times(1)).save(any(Watchlist.class));
    }

    @Test
    void testAddToWatchlist_whenMovieAlreadyExists_throwsIllegalArgumentException() {
        // Arrange
        String userId = "user-1";
        String movieId = "movie-1";

        when(watchlistRepository.existsByUserIdAndMovieId(userId, movieId)).thenReturn(true);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> watchlistService.addToWatchlist(userId, movieId)
        );

        assertEquals("Movie already exists in watchlist", exception.getMessage());
        verify(watchlistRepository, never()).save(any(Watchlist.class));
    }

    @Test
    void testAddToWatchlist_whenDuplicateKeyExceptionThrown_throwsIllegalArgumentException() {
        // Arrange
        String userId = "user-1";
        String movieId = "movie-1";

        when(watchlistRepository.existsByUserIdAndMovieId(userId, movieId)).thenReturn(false);
        when(watchlistRepository.save(any(Watchlist.class)))
                .thenThrow(new DuplicateKeyException("Duplicate key"));

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> watchlistService.addToWatchlist(userId, movieId)
        );

        assertEquals("Movie already exists in watchlist", exception.getMessage());
    }

    @Test
    void testGetUserWatchlist_existingUserRequested_returnsWatchlist() {
        // Arrange
        String userId = "user-1";
        List<Watchlist> watchlist = List.of(
                new Watchlist(userId, "movie-1"),
                new Watchlist(userId, "movie-2")
        );

        when(watchlistRepository.findByUserIdOrderByCreatedAtDesc(userId)).thenReturn(watchlist);

        // Act
        List<Watchlist> result = watchlistService.getUserWatchlist(userId);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(watchlist, result);
    }

    @Test
    void testRemoveFromWatchlist_existingEntryRequested_deletesSuccessfully() {
        // Arrange
        String userId = "user-1";
        String movieId = "movie-1";

        // Act
        watchlistService.removeFromWatchlist(userId, movieId);

        // Assert
        verify(watchlistRepository, times(1)).deleteByUserIdAndMovieId(userId, movieId);
    }
}