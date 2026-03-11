package ro.unibuc.prodeng.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import ro.unibuc.prodeng.model.SubscriptionEntity;

@Repository
public interface SubscriptionRepository extends MongoRepository<SubscriptionEntity, String> {

}
