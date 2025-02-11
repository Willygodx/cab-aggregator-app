package by.modsen.ratingservice.exception.validation;

import java.util.List;

public record ValidationResponse(

    List<Validation> errors

) {
}
