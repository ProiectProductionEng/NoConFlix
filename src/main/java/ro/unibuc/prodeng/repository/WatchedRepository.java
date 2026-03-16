package ro.unibuc.prodeng.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import ro.unibuc.prodeng.model.Watched;

import java.util.List;
import java.util.Optional;

public interface WatchedRepository extends MongoRepository<Watched, String> {

    Optional<Watched> findByUserIdAndMovieId(String userId, String movieId);

    List<Watched> findByUserIdOrderByUpdatedAtDesc(String userId);
}