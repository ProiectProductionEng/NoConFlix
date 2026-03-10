package ro.unibuc.prodeng.request;

import jakarta.validation.constraints.NotBlank;

public record CreateReviewRequest(

        @NotBlank(message = "Movie id is required")
        String movieId,

        @NotBlank(message = "User id is required")
        String userId,

        @NotBlank(message = "Comment cannot be empty")
        String comment

) {}