package ro.unibuc.prodeng.response;

public record MovieWithRatingResponse(
        String id,
        String title,
        String description,
        Integer totalViews,
        double averageRating
) {}
