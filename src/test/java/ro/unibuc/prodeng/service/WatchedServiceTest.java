package ro.unibuc.prodeng.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ro.unibuc.prodeng.model.MovieEntity;
import ro.unibuc.prodeng.model.Watched;
import ro.unibuc.prodeng.repository.MovieRepository;
import ro.unibuc.prodeng.repository.WatchedRepository;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
class WatchedServiceTest {

    @Mock
    private WatchedRepository watchedRepository;

    @Mock
    private MovieRepository movieRepository;

    @InjectMocks
    private WatchedService watchedService;

    @Test
    void testUpdateProgress_whenWatchedEntryAlreadyExists_updatesAndReturnsWatched() {
        // Arrange
        String userId = "user-1";
        String movieId = "movie-1";
        Watched existingWatched = new Watched(userId, movieId, 30);
        Instant oldUpdatedAt = existingWatched.getUpdatedAt();

        when(watchedRepository.findByUserIdAndMovieId(userId, movieId)).thenReturn(Optional.of(existingWatched));
        when(watchedRepository.save(any(Watched.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Watched result = watchedService.updateProgress(userId, movieId, 50);

        // Assert
        assertNotNull(result);
        assertEquals(userId, result.getUserId());
        assertEquals(movieId, result.getMovieId());
        assertEquals(50, result.getLastTime());
        assertNotNull(result.getUpdatedAt());
        assertTrue(!result.getUpdatedAt().isBefore(oldUpdatedAt));

        verify(watchedRepository, times(1)).findByUserIdAndMovieId(userId, movieId);
        verify(watchedRepository, times(1)).save(existingWatched);
    }

    @Test
    void testUpdateProgress_whenWatchedEntryDoesNotExist_createsAndReturnsWatched() {
        // Arrange
        String userId = "user-1";
        String movieId = "movie-1";

        when(watchedRepository.findByUserIdAndMovieId(userId, movieId)).thenReturn(Optional.empty());
        when(watchedRepository.save(any(Watched.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Watched result = watchedService.updateProgress(userId, movieId, 40);

        // Assert
        assertNotNull(result);
        assertEquals(userId, result.getUserId());
        assertEquals(movieId, result.getMovieId());
        assertEquals(40, result.getLastTime());

        verify(watchedRepository, times(1)).findByUserIdAndMovieId(userId, movieId);
        verify(watchedRepository, times(1)).save(any(Watched.class));
    }

    @Test
    void testGetContinueWatching_whenMovieDoesNotExist_filtersItOut() {
        // Arrange
        String userId = "user-1";
        Watched watched = new Watched(userId, "movie-1", 20);

        when(watchedRepository.findByUserIdOrderByUpdatedAtDesc(userId)).thenReturn(List.of(watched));
        when(movieRepository.findById("movie-1")).thenReturn(Optional.empty());

        // Act
        List<Watched> result = watchedService.getContinueWatching(userId);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(watchedRepository, times(1)).findByUserIdOrderByUpdatedAtDesc(userId);
        verify(movieRepository, times(1)).findById("movie-1");
    }

    @Test
    void testGetContinueWatching_whenMovieExistsAndIsNotFinished_returnsWatchedEntry() {
        // Arrange
        String userId = "user-1";
        Watched watched = new Watched(userId, "movie-1", 20);
        MovieEntity movie = new MovieEntity(
                "movie-1",
                "Test Movie",
                "Description",
                "genre-1",
                "2024-01-01",
                120,
                0,
                "thumb.jpg",
                "video.mp4"
        );

        when(watchedRepository.findByUserIdOrderByUpdatedAtDesc(userId)).thenReturn(List.of(watched));
        when(movieRepository.findById("movie-1")).thenReturn(Optional.of(movie));

        // Act
        List<Watched> result = watchedService.getContinueWatching(userId);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("movie-1", result.get(0).getMovieId());

        verify(watchedRepository, times(1)).findByUserIdOrderByUpdatedAtDesc(userId);
        verify(movieRepository, times(1)).findById("movie-1");
    }

    @Test
    void testGetContinueWatching_whenMovieExistsAndIsFinished_filtersItOut() {
        // Arrange
        String userId = "user-1";
        Watched watched = new Watched(userId, "movie-1", 120);
        MovieEntity movie = new MovieEntity(
                "movie-1",
                "Test Movie",
                "Description",
                "genre-1",
                "2024-01-01",
                120,
                0,
                "thumb.jpg",
                "video.mp4"
        );

        when(watchedRepository.findByUserIdOrderByUpdatedAtDesc(userId)).thenReturn(List.of(watched));
        when(movieRepository.findById("movie-1")).thenReturn(Optional.of(movie));

        // Act
        List<Watched> result = watchedService.getContinueWatching(userId);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(watchedRepository, times(1)).findByUserIdOrderByUpdatedAtDesc(userId);
        verify(movieRepository, times(1)).findById("movie-1");
    }

    @Test
    void testGetAllWatchedForUser_whenMovieDoesNotExist_filtersItOut() throws Exception {
        // Arrange
        String userId = "user-1";
        Watched watched = new Watched(userId, "movie-1", 118);

        when(watchedRepository.findByUserIdOrderByUpdatedAtDesc(userId)).thenReturn(List.of(watched));
        when(movieRepository.findById("movie-1")).thenReturn(Optional.empty());

        // Act
        List<Watched> result = watchedService.getAllWatchedForUser(userId);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(watchedRepository, times(1)).findByUserIdOrderByUpdatedAtDesc(userId);
        verify(movieRepository, times(1)).findById("movie-1");
    }

    @Test
    void testGetAllWatchedForUser_whenMovieExistsAndRemainingTimeIsAtMostFive_returnsWatchedEntry() throws Exception {
        // Arrange
        String userId = "user-1";
        Watched watched = new Watched(userId, "movie-1", 118);
        MovieEntity movie = new MovieEntity(
                "movie-1",
                "Test Movie",
                "Description",
                "genre-1",
                "2024-01-01",
                120,
                0,
                "thumb.jpg",
                "video.mp4"
        );

        when(watchedRepository.findByUserIdOrderByUpdatedAtDesc(userId)).thenReturn(List.of(watched));
        when(movieRepository.findById("movie-1")).thenReturn(Optional.of(movie));

        // Act
        List<Watched> result = watchedService.getAllWatchedForUser(userId);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("movie-1", result.get(0).getMovieId());

        verify(watchedRepository, times(1)).findByUserIdOrderByUpdatedAtDesc(userId);
        verify(movieRepository, times(1)).findById("movie-1");
    }

    @Test
    void testGetAllWatchedForUser_whenMovieExistsAndRemainingTimeIsGreaterThanFive_filtersItOut() throws Exception {
        // Arrange
        String userId = "user-1";
        Watched watched = new Watched(userId, "movie-1", 100);
        MovieEntity movie = new MovieEntity(
                "movie-1",
                "Test Movie",
                "Description",
                "genre-1",
                "2024-01-01",
                120,
                0,
                "thumb.jpg",
                "video.mp4"
        );

        when(watchedRepository.findByUserIdOrderByUpdatedAtDesc(userId)).thenReturn(List.of(watched));
        when(movieRepository.findById("movie-1")).thenReturn(Optional.of(movie));

        // Act
        List<Watched> result = watchedService.getAllWatchedForUser(userId);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(watchedRepository, times(1)).findByUserIdOrderByUpdatedAtDesc(userId);
        verify(movieRepository, times(1)).findById("movie-1");
    }
}