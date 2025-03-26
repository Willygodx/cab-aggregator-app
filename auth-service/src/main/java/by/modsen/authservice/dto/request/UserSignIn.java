package by.modsen.authservice.dto.request;

import by.modsen.authservice.constants.ApplicationConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Schema(description = "Data Transfer Object for user signing in")
public record UserSignIn(

    @Email(message = "{email.invalid.message}")
    @NotBlank(message = "{email.blank.message}")
    @Schema(description = "User's email", example = "ruslan322@example.com")
    String email,

    @NotBlank(message = "{password.blank.message}")
    @Size(min = 8, message = "{password.size.message}")
    @Pattern(
        regexp = ApplicationConstants.PASSWORD_REGEX,
        message = "{password.pattern.message}"
    )
    @Schema(description = "User's password", example = "Password123!")
    String password

) {
}
