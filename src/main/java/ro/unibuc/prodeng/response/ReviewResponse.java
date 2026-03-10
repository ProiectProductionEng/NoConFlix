package ro.unibuc.prodeng.response;

import java.time.Instant;

public record ReviewResponse(

        String id,
        String movieId,
        String userId,
        String comment,
        Instant createdAt

) {}