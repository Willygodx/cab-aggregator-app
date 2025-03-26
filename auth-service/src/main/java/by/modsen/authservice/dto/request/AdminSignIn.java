package by.modsen.authservice.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Data Transfer Object for admin signing in")
public record AdminSignIn(

    @NotBlank(message = "{grantType.blank.message}")
    @Schema(description = "Grant type for authentication", example = "client_credentials")
    String grantType,

    @NotBlank(message = "{clientId.blank.message}")
    @Schema(description = "Client ID for authentication", example = "your-client-id")
    String clientId,

    @NotBlank(message = "{clientSecret.blank.message}")
    @Schema(description = "Client secret for authentication", example = "your-client-secret")
    String clientSecret

) {
}
