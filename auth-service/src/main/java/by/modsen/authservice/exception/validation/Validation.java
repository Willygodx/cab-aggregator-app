package by.modsen.authservice.exception.validation;

public record Validation(

    String fieldName,
    String message

) {
}
