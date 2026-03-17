package ro.unibuc.prodeng.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.TextIndexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "movies")
public record MovieEntity(
    @Id String id,

    @TextIndexed
    String  title,

    @TextIndexed
    String  description,
    
    String  genreId,
    String  releaseDate,
    Integer duration,
    Integer totalViews,
    String  thumbnailUrl,
    String  videoUrl
) {}
