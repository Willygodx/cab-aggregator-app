package by.modsen.passengerservice.dto.request;

import by.modsen.passengerservice.constants.ApplicationConstants;
import by.modsen.passengerservice.dto.Marker;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

@Schema(description = "Data Transfer Object Request for Passenger")
public record PassengerRequest(

    @NotBlank(groups = {Marker.OnCreate.class}, message = "{firstname.blank.message}")
    @Schema(description = "First name of the passenger", example = "Ruslan")
    String firstName,

    @NotBlank(groups = {Marker.OnCreate.class}, message = "{lastname.blank.message}")
    @Schema(description = "Last name of the passenger", example = "Alhava")
    String lastName,

    @Email(groups = {Marker.OnCreate.class, Marker.OnUpdate.class}, message = "{email.invalid.message}")
    @NotBlank(groups = {Marker.OnCreate.class}, message = "{email.blank.message}")
    @Schema(description = "Passenger's email", example = "ruslan322@example.com")
    String email,

    @Pattern(groups = {Marker.OnCreate.class, Marker.OnUpdate.class},
        regexp = ApplicationConstants.PHONE_REGEX,
        message = "{phone.invalid.message}")
    @NotBlank(groups = {Marker.OnCreate.class}, message = "{phone.blank.message}")
    @Schema(description = "Passenger's phone number", example = "+375338723636")
    String phoneNumber,

    @Schema(description = "Keycloak user ID", example = "a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11")
    String keycloakId

) {
}
