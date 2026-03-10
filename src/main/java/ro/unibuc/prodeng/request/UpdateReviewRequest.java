package ro.unibuc.prodeng.request;

import jakarta.validation.constraints.NotBlank;

public record UpdateReviewRequest(

        @NotBlank
        String userId,

        @NotBlank
        String comment

) {}