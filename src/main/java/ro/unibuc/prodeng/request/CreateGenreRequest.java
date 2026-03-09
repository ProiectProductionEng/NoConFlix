package ro.unibuc.prodeng.request;

import jakarta.validation.constraints.NotBlank;

public record CreateGenreRequest(
    @NotBlank(message = "name is required")
    String  name
) {}