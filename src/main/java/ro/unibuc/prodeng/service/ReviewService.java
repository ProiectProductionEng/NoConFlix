package ro.unibuc.prodeng.service;

import org.springframework.stereotype.Service;
import ro.unibuc.prodeng.model.ReviewEntity;
import ro.unibuc.prodeng.repository.ReviewRepository;
import ro.unibuc.prodeng.request.CreateReviewRequest;
import ro.unibuc.prodeng.response.ReviewResponse;

import java.time.Instant;
import java.util.List;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;

    public ReviewService(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    public ReviewResponse addReview(CreateReviewRequest request) {

        ReviewEntity review = new ReviewEntity();
        review.setUserId(request.userId());
        review.setMovieId(request.movieId());
        review.setComment(request.comment());
        review.setCreatedAt(Instant.now());

        ReviewEntity saved = reviewRepository.save(review);

        return new ReviewResponse(
                saved.getId(),
                saved.getMovieId(),
                saved.getUserId(),
                saved.getComment(),
                saved.getCreatedAt()
        );
    }

    public void deleteReview(String id) {
        reviewRepository.deleteById(id);
    }

    public List<ReviewResponse> getReviewsForMovie(String movieId) {

        return reviewRepository.findByMovieId(movieId)
                .stream()
                .map(r -> new ReviewResponse(
                        r.getId(),
                        r.getMovieId(),
                        r.getUserId(),
                        r.getComment(),
                        r.getCreatedAt()))
                .toList();
    }
}