package ro.unibuc.prodeng.service;

import org.springframework.stereotype.Service;

import ro.unibuc.prodeng.exception.EntityNotFoundException;
import ro.unibuc.prodeng.model.ReviewEntity;
import ro.unibuc.prodeng.repository.ReviewRepository;
import ro.unibuc.prodeng.request.CreateReviewRequest;
import ro.unibuc.prodeng.request.UpdateReviewRequest;
import ro.unibuc.prodeng.response.ReviewResponse;

import java.time.Instant;
import java.util.List;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserService userService;

    public ReviewService(ReviewRepository reviewRepository, UserService userService) {
        this.reviewRepository = reviewRepository;
        this.userService = userService;
    }


    public ReviewResponse addReview(CreateReviewRequest request) throws EntityNotFoundException {

        userService.getUserEntityById(request.userId());

        ReviewEntity existing = reviewRepository.findByUserIdAndMovieId(request.userId(), request.movieId()).orElse(null);

        if (existing != null) {
            throw new IllegalArgumentException("User already reviewed this movie");
        }

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

    public void deleteReview(String id, String userId) throws EntityNotFoundException {

        ReviewEntity review = reviewRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(id));

        userService.getUserEntityById(userId);

        if (!review.getUserId().equals(userId)) {
            throw new IllegalArgumentException("User not allowed to delete this review");
        }

        reviewRepository.delete(review);
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


    public ReviewResponse updateReview(String id, UpdateReviewRequest request)
        throws EntityNotFoundException {

        ReviewEntity review = reviewRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(id));

        userService.getUserEntityById(request.userId());

        if (!review.getUserId().equals(request.userId())) {
            throw new IllegalArgumentException("User not allowed to update this review");
        }

        review.setComment(request.comment());

        ReviewEntity saved = reviewRepository.save(review);

        return new ReviewResponse(
                saved.getId(),
                saved.getMovieId(),
                saved.getUserId(),
                saved.getComment(),
                saved.getCreatedAt()
        );
    }
}