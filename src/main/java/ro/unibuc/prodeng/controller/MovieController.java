package ro.unibuc.prodeng.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import ro.unibuc.prodeng.request.EditMovieRequest;
import ro.unibuc.prodeng.request.CreateMovieRequest;
import ro.unibuc.prodeng.response.MovieResponse;
import ro.unibuc.prodeng.response.MovieWithRatingResponse;
import ro.unibuc.prodeng.exception.EntityNotFoundException;
import ro.unibuc.prodeng.service.MovieService;
import ro.unibuc.prodeng.service.MetricsService;

@RestController
@RequestMapping("/api/movies")
public class MovieController {

    @Autowired
    private MovieService movieService;

    @Autowired
    private MetricsService metricsService;

    @GetMapping
    public ResponseEntity<List<MovieResponse>> getAllMovies() {

        long start = System.currentTimeMillis();

        List<MovieResponse> movies = movieService.getAllMovies();

        metricsService.setTotalMovies(movies.size());
        metricsService.setActiveUsers(1); // simplu pentru lab

        metricsService.getRequestTimer()
                .record(System.currentTimeMillis() - start, java.util.concurrent.TimeUnit.MILLISECONDS);

        return ResponseEntity.ok(movies);
    }

    @PostMapping
    public ResponseEntity<MovieResponse> createMovie(@RequestBody CreateMovieRequest request) {
        try {
            MovieResponse movie = movieService.createMovie(request);

            metricsService.incrementMoviesAdded();   // business metric

            return ResponseEntity.status(HttpStatus.CREATED).body(movie);
        } catch (Exception e) {
            metricsService.incrementErrors();        // error metric
            throw e;
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<MovieResponse> updateMovie(
            @PathVariable String id,
            @Valid @RequestBody EditMovieRequest request) throws EntityNotFoundException {

        MovieResponse movie = movieService.editMovie(id, request);
        return ResponseEntity.ok(movie);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMovie(@PathVariable String id) throws EntityNotFoundException {
        try {
            movieService.deleteMovie(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            metricsService.incrementErrors();        // error metric
            throw e;
        }
    }

    @GetMapping("/recommended/{uid}")
    public ResponseEntity<List<MovieResponse>> getRecommendedMovies(@PathVariable String uid) throws EntityNotFoundException {
        List<MovieResponse> movies = movieService.getRecommendedMovies(uid);
        return ResponseEntity.ok(movies);
    }

    @GetMapping("/{uid}/watching/{id}")
    public ResponseEntity<MovieResponse> watchingMovie(@PathVariable String uid, @PathVariable String id) throws EntityNotFoundException {
        MovieResponse watchedMovie = movieService.watchMovie(id, uid);
        return ResponseEntity.ok(watchedMovie);
    }

    @GetMapping("/sorted/rating")
    public ResponseEntity<List<MovieWithRatingResponse>> getMoviesByRating() {
        return ResponseEntity.ok(movieService.getMoviesSortedByRating());
    }

    @GetMapping("/sorted/views")
    public ResponseEntity<List<MovieResponse>> getMoviesByViews() {
        return ResponseEntity.ok(movieService.getMoviesSortedByViews());
    }

    @GetMapping("/search")
    public ResponseEntity<List<MovieResponse>> searchMovies(@RequestParam String query) {
        return ResponseEntity.ok(movieService.searchMovies(query));
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<MovieResponse> getMovieById(@PathVariable String id) throws EntityNotFoundException {
        try {
            MovieResponse movie = movieService.getMovieById(id);
            return ResponseEntity.ok(movie);
        } catch (Exception e) {
            metricsService.incrementErrors();
            throw e;
        }
    }
}