package ro.unibuc.prodeng.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ro.unibuc.prodeng.request.CreateReviewRequest;
import ro.unibuc.prodeng.response.ReviewResponse;
import ro.unibuc.prodeng.service.ReviewService;

@RestController
@RequestMapping("/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping
    public ReviewResponse addReview(@RequestBody CreateReviewRequest request) {
        return reviewService.addReview(request);
    }

    @DeleteMapping("/{id}")
    public void deleteReview(@PathVariable String id) {
        reviewService.deleteReview(id);
    }

    @GetMapping("/movie/{movieId}")
    public List<ReviewResponse> getReviews(@PathVariable String movieId) {
        return reviewService.getReviewsForMovie(movieId);
    }
}
