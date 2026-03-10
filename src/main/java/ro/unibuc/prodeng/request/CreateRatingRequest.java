package ro.unibuc.prodeng.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record CreateRatingRequest(

        @NotBlank(message = "Movie id is required")
        String movieId,

        @NotBlank(message = "User id is required")
        String userId,

        @Min(value = 1, message = "Rating must be at least 1")
        @Max(value = 5, message = "Rating must be at most 5")
        int value

) {}
