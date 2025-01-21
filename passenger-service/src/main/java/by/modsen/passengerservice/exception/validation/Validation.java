package by.modsen.passengerservice.exception.validation;

public record Validation(
    String fieldName,
    String message
) {
}
