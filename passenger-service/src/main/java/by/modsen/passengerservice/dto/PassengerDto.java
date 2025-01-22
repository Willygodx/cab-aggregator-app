package by.modsen.passengerservice.dto;

import by.modsen.passengerservice.constants.ApplicationConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.Pattern;

@Schema(description = "Data Transfer Object for Passenger")
public record PassengerDto (

    @NotNull(groups = Marker.OnGet.class)
    @Null(groups = {Marker.OnCreate.class}, message = "{id.not_null.message}")
    @Schema(hidden = true)
    Long id,

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

    @Null(groups = {Marker.OnCreate.class}, message = "{is_deleted.not_null.message}")
    @Schema(description = "Passenger's status for safe delete (true if deleted and false if not deleted)", example = "true")
    Boolean isDeleted
) {
}