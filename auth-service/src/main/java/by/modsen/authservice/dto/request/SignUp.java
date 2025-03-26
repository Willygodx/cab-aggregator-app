package by.modsen.authservice.dto.request;

import by.modsen.authservice.constants.ApplicationConstants;
import by.modsen.authservice.dto.enums.role.ValidRole;
import by.modsen.authservice.dto.enums.sex.ValidSex;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Schema(description = "Data Transfer Object for signing up")
public record SignUp(

    @NotBlank(message = "{firstname.blank.message}")
    @Schema(description = "First name of the user", example = "Ruslan")
    String firstName,

    @NotBlank(message = "{lastname.blank.message}")
    @Schema(description = "Last name of the user", example = "Alhava")
    String lastName,

    @Email(message = "{email.invalid.message}")
    @NotBlank(message = "{email.blank.message}")
    @Schema(description = "User's email", example = "ruslan322@example.com")
    String email,

    @Pattern(
        regexp = ApplicationConstants.PHONE_REGEX,
        message = "{phone.invalid.message}"
    )
    @NotBlank(message = "{phone.blank.message}")
    @Schema(description = "User's phone number", example = "+375338723636")
    String phoneNumber,

    @NotBlank(message = "{password.blank.message}")
    @Size(min = 8, message = "{password.size.message}")
    @Pattern(
        regexp = ApplicationConstants.PASSWORD_REGEX,
        message = "{password.pattern.message}"
    )
    @Schema(description = "User's password", example = "Password123!")
    String password,

    @ValidSex
    @NotNull(message = "{sex.blank.message}")
    @Schema(description = "User's sex", example = "MALE")
    String sex,

    @ValidRole
    @NotNull(message = "{role.blank.message}")
    @Schema(description = "User's role", example = "PASSENGER")
    String role

) {
}
