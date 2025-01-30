package by.modsen.ridesservice.exception.validation;

import java.util.List;

public record ValidationResponse(

    List<Validation> errors

) {
}
