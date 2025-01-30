package by.modsen.ridesservice.model.enums;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Constraint(validatedBy = RideStatusValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidRideStatus {

  String message() default "{ride_status.invalid.value.message}";
  Class<?>[] groups() default {};
  Class<? extends Payload>[] payload() default {};

}
