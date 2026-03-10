package ro.unibuc.prodeng.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import ro.unibuc.prodeng.exception.EntityNotFoundException;
import ro.unibuc.prodeng.request.CreateRatingRequest;
import ro.unibuc.prodeng.request.UpdateRatingRequest;
import ro.unibuc.prodeng.response.RatingResponse;
import ro.unibuc.prodeng.service.RatingService;

@RestController
@RequestMapping("/api/ratings")
public class RatingController {

    private final RatingService ratingService;

    public RatingController(RatingService ratingService) {
        this.ratingService = ratingService;
    }

    @PostMapping
    public ResponseEntity<RatingResponse> addRating(@Valid @RequestBody CreateRatingRequest request) {
        RatingResponse response = ratingService.addRating(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRating(@PathVariable String id, @RequestParam String userId) throws EntityNotFoundException {
        ratingService.deleteRating(id, userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/movie/{movieId}")
    public ResponseEntity<List<RatingResponse>> getRatings(@PathVariable String movieId) {
        List<RatingResponse> ratings = ratingService.getRatingsForMovie(movieId);
        return ResponseEntity.ok(ratings);
    }

    @PutMapping("/{id}")
    public ResponseEntity<RatingResponse> updateRating(@PathVariable String id,@Valid @RequestBody UpdateRatingRequest request) throws EntityNotFoundException {
        RatingResponse response = ratingService.updateRating(id, request);
        return ResponseEntity.ok(response);
    }
}