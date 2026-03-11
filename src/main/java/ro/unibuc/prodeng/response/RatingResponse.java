package ro.unibuc.prodeng.response;

public record RatingResponse(

        String id,
        String movieId,
        String userId,
        int value

) {}