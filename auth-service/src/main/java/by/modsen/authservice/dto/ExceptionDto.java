package by.modsen.authservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import org.springframework.http.HttpStatus;

@Schema(description = "Data Transfer Object for exceptions")
public record ExceptionDto(

    @Schema(description = "Error message describing the issue", example = "Resource not found")
    String message,

    @Schema(description = "HTTP status of the error", example = "404")
    HttpStatus status,

    @Schema(description = "Timestamp when the error occurred", example = "2023-09-30T15:30:00")
    LocalDateTime timestamp

) {
}
