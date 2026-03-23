package ro.unibuc.prodeng.repository;

import ro.unibuc.prodeng.model.Watched;
import ro.unibuc.prodeng.model.Watchlist;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface WatchlistRepository extends MongoRepository<Watchlist, String> {

    boolean existsByUserIdAndMovieId(String userId, String movieId);

    Optional<Watchlist> findByUserIdAndMovieId(String userId, String movieId);

    List<Watchlist> findByUserIdOrderByCreatedAtDesc(String userId);
    List<Watchlist> findByUserId(String userId);
    void deleteByUserIdAndMovieId(String userId, String movieId);
}