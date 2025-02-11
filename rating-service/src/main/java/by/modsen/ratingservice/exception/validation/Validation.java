package by.modsen.ratingservice.exception.validation;

public record Validation(

    String fieldName,
    String message

) {
}
