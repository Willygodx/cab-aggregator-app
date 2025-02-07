package by.modsen.ridesservice.model.enums;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Arrays;

public class RideStatusValidator implements ConstraintValidator<ValidRideStatus, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isEmpty()) {
            return true;
        }

        return Arrays.stream(RideStatus.values())
            .anyMatch(rideStatusEnum -> rideStatusEnum.name().equals(value));
    }

}
