package ro.unibuc.prodeng.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "subscriptions")
public record SubscriptionEntity(
    @Id String id,
    String  userId,
    String  name,
    Float   price,
    Integer duration,
    String end_date
) {}
