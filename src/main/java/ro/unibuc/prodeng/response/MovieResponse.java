package ro.unibuc.prodeng.response;
public record MovieResponse(
    String  id,
    String  title,
    String  description,
    String genreId,
    String  releaseDate,
    Integer duration,
    Integer totalViews,
    String  thumbnailUrl,
    String  videoUrl
) {}
