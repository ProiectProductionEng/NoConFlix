package ro.unibuc.prodeng.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "availabilities")
public record AvailabilityEntity(
    @Id String id,
    String  movieId,
    String subscriptionId,
    String availableUntil_date
) {}
