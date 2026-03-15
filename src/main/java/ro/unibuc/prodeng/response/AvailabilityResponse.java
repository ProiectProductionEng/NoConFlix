package ro.unibuc.prodeng.response;

public record AvailabilityResponse (
    String id,
    String movieId,
    String subscriptionId,
    String availableUntil_date
){}
