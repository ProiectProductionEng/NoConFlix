package ro.unibuc.prodeng.service;

import org.springframework.stereotype.Service;

import ro.unibuc.prodeng.exception.EntityNotFoundException;
import ro.unibuc.prodeng.model.RatingEntity;
import ro.unibuc.prodeng.repository.RatingRepository;
import ro.unibuc.prodeng.request.CreateRatingRequest;
import ro.unibuc.prodeng.request.UpdateRatingRequest;
import ro.unibuc.prodeng.response.RatingResponse;

import java.util.List;
import java.util.Optional;

@Service
public class RatingService {

    private final RatingRepository ratingRepository;
    private final UserService userService;

    public RatingService(RatingRepository ratingRepository, UserService userService) {
        this.ratingRepository = ratingRepository;
        this.userService = userService;
    }

    public RatingResponse addRating(CreateRatingRequest request) throws EntityNotFoundException {
    
    //verific existenta userului
    userService.getUserEntityById(request.userId());

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

    public void deleteRating(String id, String userId) throws EntityNotFoundException {

        RatingEntity rating = ratingRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(id));

        userService.getUserEntityById(userId);

        if (!rating.getUserId().equals(userId)) {
            throw new IllegalArgumentException("User not allowed to delete this rating");
        }

        ratingRepository.delete(rating);
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


    public RatingResponse updateRating(String id, UpdateRatingRequest request) throws EntityNotFoundException {

    RatingEntity rating = ratingRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException(id));

    userService.getUserEntityById(request.userId());

    if (!rating.getUserId().equals(request.userId())) {
        throw new IllegalArgumentException("User not allowed to update this rating");
    }

    rating.setValue(request.value());

    RatingEntity saved = ratingRepository.save(rating);

    return new RatingResponse(
            saved.getId(),
            saved.getMovieId(),
            saved.getUserId(),
            saved.getValue()
    );
}
}