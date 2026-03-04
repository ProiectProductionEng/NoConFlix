package ro.unibuc.prodeng.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import ro.unibuc.prodeng.model.ReviewEntity;

import java.util.List;

public interface ReviewRepository extends MongoRepository<ReviewEntity, String> {

    List<ReviewEntity> findByMovieId(String movieId);

}
