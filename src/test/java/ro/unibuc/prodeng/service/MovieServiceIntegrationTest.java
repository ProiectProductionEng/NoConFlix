package ro.unibuc.prodeng.service;

import ro.unibuc.prodeng.IntegrationTestBase;
import ro.unibuc.prodeng.model.AvailabilityEntity;
import ro.unibuc.prodeng.model.MovieEntity;
import ro.unibuc.prodeng.model.Watched;
import ro.unibuc.prodeng.model.Watchlist;
import ro.unibuc.prodeng.model.SubscriptionEntity;
import ro.unibuc.prodeng.repository.AvailabilityRepository;
import ro.unibuc.prodeng.repository.MovieRepository;
import ro.unibuc.prodeng.repository.SubscriptionRepository;
import ro.unibuc.prodeng.repository.WatchedRepository;
import ro.unibuc.prodeng.repository.WatchlistRepository;
import ro.unibuc.prodeng.response.MovieResponse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.*;

import java.time.Instant;
import java.util.List;

@DisplayName("MovieService Integration Tests")
@Tag("IntegrationTest")
class MovieServiceIntegrationTest extends IntegrationTestBase {

    @Autowired
    private MovieService movieService;

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private SubscriptionRepository subscriptionRepository;

    @Autowired
    private WatchlistRepository watchlistRepository;

    @Autowired
    private WatchedRepository watchedRepository;

    @Autowired
    private AvailabilityRepository availabilityRepository;

    @BeforeEach
    void cleanUp() {
        availabilityRepository.deleteAll();
        movieRepository.deleteAll();
        subscriptionRepository.deleteAll();
        watchlistRepository.deleteAll();
        watchedRepository.deleteAll();
    }

    @Test
    void testWatchMovie_withValidSubscriptionAndAvailability_returnsMovieResponse() {
        // Arrange
        String movieId = "movie-1";
        String userId = "user-1";
        String subscriptionId = "sub-1";

        SubscriptionEntity subscription = new SubscriptionEntity(
                subscriptionId,
                userId,
                "premium",
                29.99f,
                30,
                "2026-12-31"
        );

        MovieEntity movie = new MovieEntity(
                movieId,
                "The Great Gatsby",
                "the best movie ever",
                "genre-commedy",
                "2026-03-21",
                3000,
                0,
                "thumb.com",
                "video.com"
        );

        AvailabilityEntity availability = new AvailabilityEntity(
                "avail-1",
                movieId,
                subscriptionId,
                "2026-12-31"
        );

        subscriptionRepository.save(subscription);
        movieRepository.save(movie);
        availabilityRepository.save(availability);

        // Act
        MovieResponse response = movieService.watchMovie(movieId, userId);

        // Assert
        assertNotNull(response);
        assertEquals(movieId, response.id());
        assertEquals("The Great Gatsby", response.title());
        assertEquals("the best movie ever", response.description());
        assertEquals("genre-commedy", response.genreId());
        assertEquals("2026-03-21", response.releaseDate());
        assertEquals(3000, response.duration());
    }

   @Test
    void testGetRecommendedMovies_returnsMoviesFromSameGenresExcludingUserMovies() {
    // Arrange
    String userId = "user-1";

    MovieEntity comedyOwned = new MovieEntity(
            "movie-1",
            "Funny Movie",
            "comedy owned by user",
            "genre-comedy",
            "2026-01-01",
            120,
            0,
            "thumb1.com",
            "video1.com"
    );

    MovieEntity actionOwned = new MovieEntity(
            "movie-2",
            "Action Movie",
            "action owned by user",
            "genre-action",
            "2026-01-02",
            130,
            0,
            "thumb2.com",
            "video2.com"
    );

    MovieEntity comedyRecommended = new MovieEntity(
            "movie-3",
            "Another Comedy",
            "should be recommended",
            "genre-comedy",
            "2026-01-03",
            110,
            0,
            "thumb3.com",
            "video3.com"
    );

    MovieEntity actionRecommended = new MovieEntity(
            "movie-4",
            "Another Action",
            "should be recommended",
            "genre-action",
            "2026-01-04",
            140,
            0,
            "thumb4.com",
            "video4.com"
    );

    MovieEntity horrorNotRecommended = new MovieEntity(
            "movie-5",
            "Horror Movie",
            "should not be recommended",
            "genre-horror",
            "2026-01-05",
            100,
            0,
            "thumb5.com",
            "video5.com"
    );

    movieRepository.save(comedyOwned);
    movieRepository.save(actionOwned);
    movieRepository.save(comedyRecommended);
    movieRepository.save(actionRecommended);
    movieRepository.save(horrorNotRecommended);

    Watchlist watchlistEntry = new Watchlist();
    watchlistEntry.setId("wl-1");
    watchlistEntry.setUserId(userId);
    watchlistEntry.setMovieId("movie-1");
    watchlistEntry.setCreatedAt(Instant.now());

    Watched watchedEntry = new Watched();
    watchedEntry.setId("wat-1");
    watchedEntry.setUserId(userId);
    watchedEntry.setMovieId("movie-2");
    watchedEntry.setLastTime(95);
    watchedEntry.setUpdatedAt(Instant.now());

    watchlistRepository.save(watchlistEntry);
    watchedRepository.save(watchedEntry);

    // Act
    List<MovieResponse> result = movieService.getRecommendedMovies(userId);

    // Assert
    assertNotNull(result);
    assertEquals(2, result.size());

    List<String> returnedIds = result.stream()
            .map(MovieResponse::id)
            .toList();

    assertTrue(returnedIds.contains("movie-3"));
    assertTrue(returnedIds.contains("movie-4"));

    assertFalse(returnedIds.contains("movie-1"));
    assertFalse(returnedIds.contains("movie-2"));
    assertFalse(returnedIds.contains("movie-5"));
}
}