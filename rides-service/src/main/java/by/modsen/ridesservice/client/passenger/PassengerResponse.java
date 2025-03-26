package by.modsen.ridesservice.client.passenger;

import java.util.UUID;

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
