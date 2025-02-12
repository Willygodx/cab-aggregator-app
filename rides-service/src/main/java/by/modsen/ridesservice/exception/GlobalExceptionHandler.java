package by.modsen.ridesservice.exception;

import by.modsen.ridesservice.client.exception.FeignClientException;
import by.modsen.ridesservice.dto.ExceptionDto;
import by.modsen.ridesservice.exception.converter.RideStatusConversionException;
import by.modsen.ridesservice.exception.ride.RideNotFoundException;
import by.modsen.ridesservice.exception.ride.RideStatusIncorrectException;
import by.modsen.ridesservice.exception.validation.Validation;
import by.modsen.ridesservice.exception.validation.ValidationResponse;
import jakarta.validation.ConstraintViolationException;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final MessageSource messageSource;

    @ExceptionHandler({
        RideStatusIncorrectException.class
    })
    @ResponseStatus(HttpStatus.CONFLICT)
    public ExceptionDto handleRideStatusIncorrectException(MessageSourceException e) {
        String message = messageSource.getMessage(e.getMessageKey(), e.getArgs(), LocaleContextHolder.getLocale());

        return new ExceptionDto(message, HttpStatus.CONFLICT, LocalDateTime.now());
    }

    @ExceptionHandler({
        RideNotFoundException.class
    })
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ExceptionDto handleRideNotFoundException(MessageSourceException e) {
        String message = messageSource.getMessage(e.getMessageKey(), e.getArgs(), LocaleContextHolder.getLocale());

        return new ExceptionDto(message, HttpStatus.NOT_FOUND, LocalDateTime.now());
    }

    @ExceptionHandler({
        Exception.class,
        RideStatusConversionException.class
    })
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ExceptionDto handleServerErrors(Exception e) {
        return new ExceptionDto(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR, LocalDateTime.now());
    }

    @ExceptionHandler({
        FeignClientException.class
    })
    public ResponseEntity<ExceptionDto> handleFeignClientException(FeignClientException e) {
        return ResponseEntity.status(e.getExceptionDto().status()).body(e.getExceptionDto());
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
