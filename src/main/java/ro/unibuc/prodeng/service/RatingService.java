package ro.unibuc.prodeng.service;

import org.springframework.stereotype.Service;
import ro.unibuc.prodeng.model.RatingEntity;
import ro.unibuc.prodeng.repository.RatingRepository;
import ro.unibuc.prodeng.request.CreateRatingRequest;
import ro.unibuc.prodeng.response.RatingResponse;

import java.util.List;
import java.util.Optional;

@Service
public class RatingService {

    private final RatingRepository ratingRepository;

    public RatingService(RatingRepository ratingRepository) {
        this.ratingRepository = ratingRepository;
    }

    public RatingResponse addRating(CreateRatingRequest request) {

        Optional<RatingEntity> existing =
                ratingRepository.findByUserIdAndMovieId(request.userId(), request.movieId());

        RatingEntity rating;

        if (existing.isPresent()) {
            rating = existing.get();
            rating.setValue(request.value());
        } else {
            rating = new RatingEntity();
            rating.setUserId(request.userId());
            rating.setMovieId(request.movieId());
            rating.setValue(request.value());
        }

        RatingEntity saved = ratingRepository.save(rating);

        return new RatingResponse(
                saved.getId(),
                saved.getMovieId(),
                saved.getUserId(),
                saved.getValue()
        );
    }

    public void deleteRating(String id) {
        ratingRepository.deleteById(id);
    }

    public List<RatingResponse> getRatingsForMovie(String movieId) {

        return ratingRepository.findByMovieId(movieId)
                .stream()
                .map(r -> new RatingResponse(
                        r.getId(),
                        r.getMovieId(),
                        r.getUserId(),
                        r.getValue()))
                .toList();
    }
}