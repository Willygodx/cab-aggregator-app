package by.modsen.ratingservice.client.passenger;

import java.util.UUID;

public record PassengerResponse(

    UUID id,
    String firstName,
    String lastName,
    String email,
    String phoneNumber,
    Double averageRating,
    Boolean isDeleted

) {
}
