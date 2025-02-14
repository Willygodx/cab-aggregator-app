package by.modsen.passengerservice.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Data Transfer Object Response for Passenger")
public record PassengerResponse(

    Long id,
    String firstName,
    String lastName,
    String email,
    String phoneNumber,
    String averageRating,
    Boolean isDeleted

) {
}
