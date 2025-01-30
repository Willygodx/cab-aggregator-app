package by.modsen.ridesservice.exception;

import by.modsen.ridesservice.dto.ExceptionDto;
import by.modsen.ridesservice.exception.converter.RideStatusConversionException;
import by.modsen.ridesservice.exception.ride.RideNotFoundException;
import by.modsen.ridesservice.exception.ride.RideStatusIncorrectException;
import by.modsen.ridesservice.exception.validation.Validation;
import by.modsen.ridesservice.exception.validation.ValidationResponse;
import jakarta.validation.ConstraintViolationException;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(RideStatusIncorrectException.class)
  @ResponseStatus(HttpStatus.CONFLICT)
  public ExceptionDto handleRideStatusIncorrectException(Exception e) {
    return new ExceptionDto(
        e.getMessage(),
        HttpStatus.CONFLICT,
        LocalDateTime.now());
  }

  @ExceptionHandler(RideNotFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public ExceptionDto handleRideNotFoundException(Exception e) {
    return new ExceptionDto(
        e.getMessage(),
        HttpStatus.NOT_FOUND,
        LocalDateTime.now());
  }

  @ExceptionHandler(
      {
          Exception.class,
          RideStatusConversionException.class
      }
  )
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public ExceptionDto handleServerErrors(Exception e) {
    return new ExceptionDto(
        e.getMessage(),
        HttpStatus.INTERNAL_SERVER_ERROR,
        LocalDateTime.now());
  }

  @ExceptionHandler(ConstraintViolationException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ValidationResponse handleConstraintValidationException(ConstraintViolationException e) {
    final List<Validation> validations = e.getConstraintViolations().stream()
        .map(
            validation -> new Validation(
                validation.getPropertyPath().toString().replaceFirst(".*\\.", ""),
                validation.getMessage()))
        .toList();
    return new ValidationResponse(validations);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ValidationResponse handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
    final List<Validation> validations = e.getBindingResult().getFieldErrors().stream()
        .map(error -> new Validation(error.getField(), error.getDefaultMessage()))
        .toList();
    return new ValidationResponse(validations);
  }

}
