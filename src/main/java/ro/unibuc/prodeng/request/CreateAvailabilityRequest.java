package ro.unibuc.prodeng.request;

import jakarta.validation.constraints.NotBlank;

public record CreateAvailabilityRequest(
    @NotBlank(message = "movieId is required")
    String movieId,
    @NotBlank(message = "subscriptionId is required")
    String subscriptionId,
    String availableUntil_date
) {}