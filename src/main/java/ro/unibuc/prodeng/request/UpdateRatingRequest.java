package ro.unibuc.prodeng.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record UpdateRatingRequest(

        @NotBlank
        String userId,

        @Min(1)
        @Max(5)
        int value

) {}