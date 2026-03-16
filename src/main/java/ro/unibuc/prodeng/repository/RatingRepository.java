package ro.unibuc.prodeng.repository;

import ro.unibuc.prodeng.model.RatingEntity;

import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface RatingRepository extends MongoRepository<RatingEntity, String> {

    Optional<RatingEntity> findByUserIdAndMovieId(String userId, String movieId);

    List<RatingEntity> findByMovieId(String movieId);

    boolean existsByUserIdAndMovieId(String userId, String movieId);

    @Aggregation(pipeline = {
        "{ $match: { movieId: ?0 } }",
        "{ $group: { _id: '$movieId', avgRating: { $avg: '$value' } } }"
    })
    Double getAverageRatingForMovie(String movieId);

}