package ro.unibuc.prodeng.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import ro.unibuc.prodeng.model.SubscriptionEntity;

@Repository
public interface SubscriptionRepository extends MongoRepository<SubscriptionEntity, String> {
    Optional<SubscriptionEntity> findByUserId(String userId);
    
}
