package ro.unibuc.prodeng.repository;

import ro.unibuc.prodeng.model.ReviewEntity;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface RatingRepository extends MongoRepository<ReviewEntity, String> {

    Optional<ReviewEntity> findByUserIdAndMovieId(String userId, String movieId);

    List<ReviewEntity> findByMovieId(String movieId);

}
