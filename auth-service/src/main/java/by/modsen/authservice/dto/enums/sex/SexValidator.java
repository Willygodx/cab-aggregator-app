package by.modsen.authservice.dto.enums.sex;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Arrays;

public class SexValidator implements ConstraintValidator<ValidSex, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isEmpty()) {
            return true;
        }

        return Arrays.stream(Sex.values())
            .anyMatch(sex -> sex.name().equals(value));
    }

}
