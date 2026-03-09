package ro.unibuc.prodeng.request;

import jakarta.validation.constraints.NotBlank;

public record EditGenreRequest(
    @NotBlank(message = "Name is required")
    String  name
) {}

