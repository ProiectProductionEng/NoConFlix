package ro.unibuc.prodeng.request;

public record EditAvailabilityRequest(
    String movieId,
    String subscriptionId,
    String availableUntil_date
) {}