package ro.unibuc.prodeng.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ro.unibuc.prodeng.request.CreateRatingRequest;
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
    public RatingResponse addRating(@RequestBody CreateRatingRequest request) {
        return ratingService.addRating(request);
    }

    @DeleteMapping("/{id}")
    public void deleteRating(@PathVariable String id) {
        ratingService.deleteRating(id);
    }

    @GetMapping("/movie/{movieId}")
    public List<RatingResponse> getRatings(@PathVariable String movieId) {
        return ratingService.getRatingsForMovie(movieId);
    }
}
