package ro.unibuc.prodeng.request;
import jakarta.validation.constraints.NotBlank;

public record CreateSubscriptionRequest(
    @NotBlank(message = "To whom are you subscribing?!")
    String  userId,
    @NotBlank(message = "Name is required")
    String  name,
    Float   price,
    Integer duration,
    String end_date
) {}