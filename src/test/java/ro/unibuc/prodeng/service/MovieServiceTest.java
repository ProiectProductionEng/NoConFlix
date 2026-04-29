package ro.unibuc.prodeng.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import ro.unibuc.prodeng.model.AvailabilityEntity;
import ro.unibuc.prodeng.model.MovieEntity;
import ro.unibuc.prodeng.model.SubscriptionEntity;
import ro.unibuc.prodeng.repository.AvailabilityRepository;
import ro.unibuc.prodeng.repository.MovieRepository;
import ro.unibuc.prodeng.repository.SubscriptionRepository;
import ro.unibuc.prodeng.request.CreateMovieRequest;
import ro.unibuc.prodeng.request.EditMovieRequest;
import ro.unibuc.prodeng.response.MovieResponse;
import ro.unibuc.prodeng.exception.EntityNotFoundException;
import ro.unibuc.prodeng.model.Watched;
import ro.unibuc.prodeng.model.Watchlist;
import ro.unibuc.prodeng.repository.WatchlistRepository;
import ro.unibuc.prodeng.repository.WatchedRepository;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
class MovieServiceTest {

    @Mock
    private WatchlistRepository watchlistRepository;
    
    @Mock
    private WatchedRepository watchedRepository;
    
    @Mock
    private AvailabilityRepository availabilityRepository;

    @Mock
    private SubscriptionRepository subscriptionRepository;

    @Mock
    private MovieRepository movieRepository;

    @InjectMocks
    private MovieService movieService;

    @InjectMocks
    private WatchedService watchedService;

    @InjectMocks
    private WatchlistService watchlistService;

    @InjectMocks
    private AvailabilityService availabilityService;

    @InjectMocks
    private SubscriptionService subscriptionService;

    private MovieEntity testMovie1 = new MovieEntity("movie-1","The Great Gatsby","the best movie ever","genre-commedy","2026-03-21",3000,5542543,"google.com","google.com");
    private MovieEntity testMovie2 = new MovieEntity("movie-2","Spiderman: No way home","newest movie","genre-horror","2026-03-22",3000,5542543,"google.com","google.com");
    private CreateMovieRequest createMovieRequest = new CreateMovieRequest("The conjuring","great","genre-thriller","1996-03-22",100,"google.com","google.com");
    private EditMovieRequest editMovieRequest = new EditMovieRequest("The conjuring","great","genre-thriller","1996-03-22",100,500,"google.com","google.com");

    @Test
    void testGetAllMovies_withMultipleMovies_returnsAllMovies() {
        // Arrange
        List<MovieEntity> movies = Arrays.asList(
                testMovie1,
                testMovie2
        );
        when(movieRepository.findAll()).thenReturn(movies);

        // Act
        List<MovieResponse> result = movieService.getAllMovies();

        // Assert
        assertEquals(2, result.size());
        assertEquals("The Great Gatsby", result.get(0).title());
        assertEquals("Spiderman: No way home", result.get(1).title());
    }

    @Test
    void testGetMovieById_existingMovieRequested_returnsMovie() throws EntityNotFoundException {
        // Arrange
        when(movieRepository.findById("movie-1")).thenReturn(Optional.of(testMovie1));

        // Act
        MovieResponse result = movieService.getMovieById("movie-1");

        // Assert
        assertNotNull(result);
        assertEquals("The Great Gatsby", result.title());
        assertEquals("the best movie ever", result.description());
    }

    @Test
    void testGetMovieEntityById_existingMovieRequested_returnsMovie() throws EntityNotFoundException {
        // Arrange
        when(movieRepository.findById("movie-1")).thenReturn(Optional.of(testMovie1));

        // Act
        MovieEntity result = movieService.getMovieEntityById("movie-1");

        // Assert
        assertNotNull(result);
        assertEquals(result,testMovie1);
    }
    
    @Test
    void testGetMovieById_nonExistingMovieRequested_throwsEntityNotFoundException() {
        // Arrange
        when(movieRepository.findById("movie-999")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> movieService.getMovieById("movie-999"));
    }

    @Test
    void testCreateMovie_newMovieWithValidData_createsAndReturnsMovie() {
        // Arrange
        when(movieRepository.save(any(MovieEntity.class))).thenAnswer(invocation -> {
            MovieEntity entity = invocation.getArgument(0);
            // Simulate MongoDB generating an ID for new entities
            String id = "movie-123";
            return new MovieEntity(id, "The conjuring","great","genre-thriller","1996-03-22",100,0,"google.com","google.com");
        });

        // Act
        MovieResponse result = movieService.createMovie(createMovieRequest);

        // Assert
        assertNotNull(result);
        assertNotNull(result.id());
        assertEquals("The conjuring", result.title());
        assertEquals("great", result.description());
        verify(movieRepository, times(1)).save(any(MovieEntity.class));
    }

    @Test
    void testEditMovie_existingMovieRequested_editsMovieSuccessfully() throws EntityNotFoundException {
        // Arrange
        when(movieRepository.findById("movie-1")).thenReturn(Optional.of(testMovie1));
        when(movieRepository.save(any(MovieEntity.class))).thenAnswer(invocation -> {
            MovieEntity entity = invocation.getArgument(0);
            // Simulate MongoDB generating an ID for new entities
            String id = entity.id() == null ? "generated-id-123" : entity.id();
            return new MovieEntity(
                id,
                entity.title(),
                entity.description(),
                entity.genreId(),
                entity.releaseDate(),
                entity.duration(),
                entity.totalViews(),
                entity.thumbnailUrl(),
                entity.videoUrl()
            );
        });

        // Act
        MovieResponse result = movieService.editMovie("movie-1",editMovieRequest);

        // Assert
        assertNotNull(result);
        assertEquals("movie-1", result.id());
        assertEquals("The conjuring", result.title());
        assertEquals("great", result.description());
        assertEquals("genre-thriller", result.genreId());
        assertEquals("1996-03-22", result.releaseDate());
        assertEquals(100, result.duration());
        assertEquals(500, result.totalViews());
        assertEquals("google.com", result.thumbnailUrl());
        assertEquals("google.com", result.videoUrl());
    }

    @Test
    void testEditMovie_nonExistingMovieRequested_throwsEntityNotFoundException() {
        // Arrange
        when(movieRepository.findById("movie-999")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> movieService.editMovie("movie-999", editMovieRequest));
    }

    @Test
    void testDeleteMovie_existingMovieRequested_deletesSuccessfully() throws EntityNotFoundException {
        // Arrange
        when(movieRepository.existsById("movie-1")).thenReturn(true);

        // Act
        movieService.deleteMovie("movie-1");

        // Assert
        verify(movieRepository, times(1)).deleteById("movie-1");
    }

    @Test
    void testDeleteMovie_nonExistingMovieRequested_throwsEntityNotFoundException() {
        // Arrange
        when(movieRepository.existsById("movie-999")).thenReturn(false);

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> movieService.deleteMovie("movie-999"));
    }

    @Test
    void searchMovies_whenTextSearchReturnsResults_shouldReturnTextResults() {
        // Arrange
        when(movieRepository.searchByText("gatsby"))
                .thenReturn(List.of(testMovie1));

        when(movieRepository.findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(anyString(), anyString()))
                .thenReturn(List.of());

        // Act
        List<MovieResponse> result = movieService.searchMovies("gatsby");

        // Assert
        assertEquals(1, result.size());
        assertEquals("The Great Gatsby", result.get(0).title());
    }

    @Test
    void searchMovies_whenTextSearchEmpty_shouldUseFallback() {
        // Arrange
        when(movieRepository.searchByText("roman"))
                .thenReturn(List.of());

        when(movieRepository.findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase("roman","roman"))
                .thenReturn(List.of(testMovie2));

        // Act
        List<MovieResponse> result = movieService.searchMovies("roman");

        // Assert
        assertEquals(1, result.size());
        assertEquals("Spiderman: No way home", result.get(0).title());
    }

    @Test
    void searchMovies_shouldCombineTextAndFallbackResults() {
        // Arrange
        when(movieRepository.searchByText("movie"))
                .thenReturn(List.of(testMovie1));

        when(movieRepository.findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase("movie","movie"))
                .thenReturn(List.of(testMovie2));

        // Act
        List<MovieResponse> result = movieService.searchMovies("movie");

        // Assert
        assertEquals(2, result.size());
    }

    @Test
    void getMoviesSortedByViews_shouldReturnDescendingOrder() {
        // Arrange
        MovieEntity m1 = new MovieEntity("1","A","",null,null,0,100,null,null);
        MovieEntity m2 = new MovieEntity("2","B","",null,null,0,50,null,null);

        when(movieRepository.findAllByOrderByTotalViewsDesc())
                .thenReturn(List.of(m1, m2));

        // Act
        List<MovieResponse> result = movieService.getMoviesSortedByViews();

        // Assert
        assertEquals("A", result.get(0).title());
        assertEquals("B", result.get(1).title());
    }

    @Test
    void getMoviesSortedByRating_shouldSortDescending() {
        // Arrange
        when(movieRepository.findAll())
                .thenReturn(List.of(testMovie1, testMovie2));

        MovieService spy = spy(movieService);

        doReturn(5.0).when(spy).getAverageRating("movie-1");
        doReturn(3.0).when(spy).getAverageRating("movie-2");

        // Act
        var result = spy.getMoviesSortedByRating();

        // Assert
        assertEquals("The Great Gatsby", result.get(0).title());
    }

    @Test
    void testWatchMovie_validSubscriptionAndAvailability_returnsMovieResponse() throws Exception {
        // Arrange
        String userId = "user-1";
        String movieId = "movie-1";

        SubscriptionEntity subscription = new SubscriptionEntity(
                "subscription-1",
                userId,
                "premium",
                10.0f,
                365,
                "2027-03-23"
        );

        AvailabilityEntity availability = new AvailabilityEntity(
                "availability1-1",
                movieId,
                subscription.id(),
                "2029-04-23"
        );

        when(subscriptionRepository.findByUserId(userId)).thenReturn(Optional.of(subscription));
        when(availabilityRepository.findByMovieIdAndSubscriptionId(movieId, subscription.id()))
                .thenReturn(Optional.of(availability));
        when(movieRepository.findById(movieId)).thenReturn(Optional.of(testMovie1));

        // Act
        MovieResponse result = movieService.watchMovie(movieId, userId);

        // Assert
        assertNotNull(result);
        assertEquals("movie-1", result.id());
        assertEquals("The Great Gatsby", result.title());
        assertEquals("the best movie ever", result.description());
        assertEquals("genre-commedy", result.genreId());
        assertEquals("2026-03-21", result.releaseDate());
        assertEquals(3000, result.duration());
        assertEquals(5542543, result.totalViews());
        assertEquals("google.com", result.thumbnailUrl());
        assertEquals("google.com", result.videoUrl());

        verify(subscriptionRepository, times(1)).findByUserId(userId);
        verify(availabilityRepository, times(1))
                .findByMovieIdAndSubscriptionId(movieId, subscription.id());
        verify(movieRepository, times(1)).findById(movieId);
    }

    @Test
    void testWatchMovie_expiredSubscription_throwsIllegalStateException() {
        // Arrange
        String userId = "user-1";
        String movieId = "movie-1";

        SubscriptionEntity subscription = new SubscriptionEntity(
                "subscription-1",
                userId,
                "premium",
                10.0f,
                365,
                "2005-03-23"
        );

        AvailabilityEntity availability = new AvailabilityEntity(
                "avail-1",
                movieId,
                subscription.id(),
                LocalDate.now().plusDays(5).toString()
        );

        when(subscriptionRepository.findByUserId(userId)).thenReturn(Optional.of(subscription));
        when(availabilityRepository.findByMovieIdAndSubscriptionId(movieId, subscription.id()))
                .thenReturn(Optional.of(availability));

        // Act + Assert
        assertThrows(IllegalStateException.class, () -> movieService.watchMovie(movieId, userId));

        verify(subscriptionRepository, times(1)).findByUserId(userId);
        verify(availabilityRepository, times(1))
                .findByMovieIdAndSubscriptionId(movieId, subscription.id());
        verify(movieRepository, never()).findById(anyString());
    }

    @Test
    void testGetRecommendedMovies_userHasWatchedMovies_returnsRecommendedMovies() {
        // Arrange
        String userId = "user-1";

        Watchlist watchlistMovie = new Watchlist( userId, "movie-1");
        Watched watchedMovie = new Watched(userId, "movie-2",50);

        MovieEntity movie1 = new MovieEntity(
                "movie-1", "Movie One", "desc1", "genre-action",
                "2026-01-01", 120, 100, "thumb1", "video1"
        );

        MovieEntity movie2 = new MovieEntity(
                "movie-2", "Movie Two", "desc2", "genre-action",
                "2026-01-02", 130, 200, "thumb2", "video2"
        );

        MovieEntity movie3 = new MovieEntity(
                "movie-3", "Movie Three", "desc3", "genre-action",
                "2026-01-03", 140, 300, "thumb3", "video3"
        );

        MovieEntity movie4 = new MovieEntity(
                "movie-4", "Movie Four", "desc4", "genre-comedy",
                "2026-01-04", 150, 400, "thumb4", "video4"
        );

        when(watchlistRepository.findByUserId(userId)).thenReturn(List.of(watchlistMovie));
        when(watchedRepository.findByUserId(userId)).thenReturn(List.of(watchedMovie));
        when(movieRepository.findAll()).thenReturn(List.of(movie1, movie2, movie3, movie4));

        // Act
        List<MovieResponse> result = movieService.getRecommendedMovies(userId);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());

        assertEquals("movie-3", result.get(0).id());
        assertEquals("Movie Three", result.get(0).title());
        assertEquals("genre-action", result.get(0).genreId());

        verify(watchlistRepository, times(1)).findByUserId(userId);
        verify(watchedRepository, times(1)).findByUserId(userId);
        verify(movieRepository, times(2)).findAll();
    }
}
