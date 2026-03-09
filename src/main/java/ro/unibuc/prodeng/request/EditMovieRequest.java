package ro.unibuc.prodeng.request;

import jakarta.validation.constraints.NotBlank;

public record EditMovieRequest(
    @NotBlank(message = "Title is required")
    String  title,
    String  description,
    String  genreId,
    String  releaseDate,
    Integer duration,
    Integer totalViews,
    String  thumbnailUrl,
    String  videoUrl
) {}

