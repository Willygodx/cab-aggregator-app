package by.modsen.passengerservice.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;

@Schema(description = "Data Transfer Object Response for Passenger")
public record PassengerResponse(

    UUID id,
    String firstName,
    String lastName,
    String email,
    String phoneNumber,
    String averageRating,
    Boolean isDeleted

) {
}
