package by.modsen.driverservice.exception;

import by.modsen.driverservice.dto.ExceptionDto;
import by.modsen.driverservice.exception.car.CarNotFoundException;
import by.modsen.driverservice.exception.car.CarNumberAlreadyExistsException;
import by.modsen.driverservice.exception.converter.SexConversionException;
import by.modsen.driverservice.exception.driver.DriverAlreadyExistsException;
import by.modsen.driverservice.exception.driver.DriverNotFoundException;
import by.modsen.driverservice.exception.validation.Validation;
import by.modsen.driverservice.exception.validation.ValidationResponse;
import jakarta.validation.ConstraintViolationException;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final MessageSource messageSource;

    @ExceptionHandler({
        CarNumberAlreadyExistsException.class,
        DriverAlreadyExistsException.class
    })
    @ResponseStatus(HttpStatus.CONFLICT)
    public ExceptionDto handleConflictExceptions(MessageSourceException e) {
        String message = messageSource.getMessage(e.getMessageKey(), e.getArgs(), LocaleContextHolder.getLocale());

        return new ExceptionDto(message, HttpStatus.CONFLICT, LocalDateTime.now());
    }

    @ExceptionHandler({
        CarNotFoundException.class,
        DriverNotFoundException.class
    })
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ExceptionDto handleNotFoundExceptions(MessageSourceException e) {
        String message = messageSource.getMessage(e.getMessageKey(), e.getArgs(), LocaleContextHolder.getLocale());

        return new ExceptionDto(message, HttpStatus.NOT_FOUND, LocalDateTime.now());
    }

    @ExceptionHandler({
        Exception.class,
        SexConversionException.class
    })
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ExceptionDto handleServerErrors(Exception e) {
        return new ExceptionDto(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR, LocalDateTime.now());
    }

    @ExceptionHandler({
        ConstraintViolationException.class
    })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ValidationResponse handleConstraintValidationException(ConstraintViolationException e) {
        final List<Validation> validations = e.getConstraintViolations().stream().map(
            validation -> new Validation(validation.getPropertyPath().toString().replaceFirst(".*\\.", ""),
                validation.getMessage())).toList();
        return new ValidationResponse(validations);
    }

    @ExceptionHandler({
        MethodArgumentNotValidException.class
    })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ValidationResponse handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        final List<Validation> validations = e.getBindingResult().getFieldErrors().stream()
            .map(error -> new Validation(error.getField(), error.getDefaultMessage())).toList();
        return new ValidationResponse(validations);
    }

}
