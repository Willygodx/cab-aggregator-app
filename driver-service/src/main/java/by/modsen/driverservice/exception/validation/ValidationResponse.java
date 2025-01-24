package by.modsen.driverservice.exception.validation;

import java.util.List;

public record ValidationResponse (

    List<Validation> errors

) {
}
