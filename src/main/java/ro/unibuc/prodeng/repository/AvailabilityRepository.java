package ro.unibuc.prodeng.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import ro.unibuc.prodeng.model.AvailabilityEntity;

@Repository
public interface AvailabilityRepository extends MongoRepository<AvailabilityEntity, String> {
    Optional<AvailabilityEntity> findByMovieIdAndSubscriptionId(String movieId, String subscriptionId);
    //List<MovieEntity> findByMovieId(String movieId);
}
