package ro.unibuc.prodeng.request;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record CreateSubscriptionRequest(
    @NotBlank(message = "To whom are you subscribing?!")
    String  userId,
    @NotBlank(message = "Name is required")
    String  name,
    @Min(value = 0, message = "Must be a passable price!")
    Float   price,
    @Min(value = 0, message = "Must be a passable duration!")
    Integer duration,
    String end_date
) {}