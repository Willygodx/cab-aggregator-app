package by.modsen.authservice.exception.validation;

import java.util.List;

public record ValidationResponse(

    List<Validation> errors

) {
}
