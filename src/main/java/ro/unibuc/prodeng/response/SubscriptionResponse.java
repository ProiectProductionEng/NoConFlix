package ro.unibuc.prodeng.response;
public record SubscriptionResponse(
    String  id,
    String  userId,
    String  name,
    Float   price,
    Integer duration,
    String end_date
) {}
