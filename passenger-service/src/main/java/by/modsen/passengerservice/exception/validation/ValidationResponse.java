package by.modsen.passengerservice.exception.validation;

import java.util.List;

public record ValidationResponse (

    List<Validation> errors

) {
}
