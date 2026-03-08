package ro.unibuc.prodeng.request;

import jakarta.validation.constraints.NotBlank;

public record CreateMovieRequest(
    @NotBlank(message = "Title is required")
    String  title,
    String  description,
    String  genreId,
    String  releaseDate,
    Integer duration,
    String  thumbnailUrl,
    String  videoUrl
) {}