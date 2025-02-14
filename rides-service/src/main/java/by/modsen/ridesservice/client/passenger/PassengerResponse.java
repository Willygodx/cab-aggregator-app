package by.modsen.ridesservice.client.passenger;

public record PassengerResponse(

    Long id,
    String firstName,
    String lastName,
    String email,
    String phoneNumber,
    Boolean isDeleted

) {
}
