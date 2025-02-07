package by.modsen.ratingservice.model.enums;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Arrays;

public class RatedByValidator implements ConstraintValidator<ValidRatedBy, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isEmpty()) {
            return true;
        }

        return Arrays.stream(RatedBy.values())
            .anyMatch(ratedBy -> ratedBy.name().equals(value));
    }

}
