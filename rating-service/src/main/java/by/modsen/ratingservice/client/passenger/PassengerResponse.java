package by.modsen.ratingservice.client.passenger;

public record PassengerResponse(

    Long id,
    String firstName,
    String lastName,
    String email,
    String phoneNumber,
    Boolean isDeleted

) {
}
