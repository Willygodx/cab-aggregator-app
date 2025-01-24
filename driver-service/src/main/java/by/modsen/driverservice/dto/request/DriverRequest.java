package by.modsen.driverservice.dto.request;

import by.modsen.driverservice.constants.ApplicationConstants;
import by.modsen.driverservice.dto.Marker;
import by.modsen.driverservice.model.enums.ValidSex;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

@Schema(description = "Data Transfer Object for Driver")
public record DriverRequest(

    @NotBlank(groups = {Marker.OnCreate.class}, message = "{firstname.blank.message}")
    @Schema(description = "First name of the driver", example = "Ruslan")
    String firstName,

    @NotBlank(groups = {Marker.OnCreate.class}, message = "{lastname.blank.message}")
    @Schema(description = "Last name of the driver", example = "Alhava")
    String lastName,

    @Email(groups = {Marker.OnCreate.class, Marker.OnUpdate.class}, message = "{email.invalid.message}")
    @NotBlank(groups = {Marker.OnCreate.class}, message = "{email.blank.message}")
    @Schema(description = "Driver's email", example = "ruslan322@example.com")
    String email,

    @Pattern(groups = {Marker.OnCreate.class, Marker.OnUpdate.class},
        regexp = ApplicationConstants.PHONE_REGEX,
        message = "{phone.invalid.message}")
    @NotBlank(groups = {Marker.OnCreate.class}, message = "{phone.blank.message}")
    @Schema(description = "Driver's phone number", example = "+375338723636")
    String phoneNumber,

    @ValidSex(groups = {Marker.OnCreate.class, Marker.OnUpdate.class})
    @NotNull(groups = {Marker.OnCreate.class}, message = "{sex.blank.message}")
    @Schema(description = "Driver's sex", example = "MALE")
    String sex

) {
}
