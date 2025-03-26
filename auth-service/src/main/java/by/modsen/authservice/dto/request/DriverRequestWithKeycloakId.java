package by.modsen.authservice.dto.request;

public record DriverRequestWithKeycloakId(

    String firstName,
    String lastName,
    String email,
    String phoneNumber,
    String sex,
    String keycloakId

) {
}
