package by.modsen.passengerservice.exception;

import by.modsen.passengerservice.constants.ApplicationExceptionMessages;
import by.modsen.passengerservice.dto.ExceptionDto;
import by.modsen.passengerservice.exception.passenger.PassengerAlreadyExistsException;
import by.modsen.passengerservice.exception.passenger.PassengerNotFoundException;
import by.modsen.passengerservice.exception.validation.Validation;
import by.modsen.passengerservice.exception.validation.ValidationResponse;
import jakarta.validation.ConstraintViolationException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(PassengerAlreadyExistsException.class)
  @ResponseStatus(HttpStatus.CONFLICT)
  public ExceptionDto handlePassengerAlreadyExistsException(Exception e) {
    return new ExceptionDto(e.getMessage(), HttpStatus.CONFLICT, LocalDateTime.now());
  }

  @ExceptionHandler(PassengerNotFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public ExceptionDto handlePassengerNotFoundException(Exception e) {
    return new ExceptionDto(e.getMessage(), HttpStatus.NOT_FOUND, LocalDateTime.now());
  }

  @ExceptionHandler(Exception.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public ExceptionDto handleInternalServerErrors(Exception e) {
    return new ExceptionDto(
        ApplicationExceptionMessages.INTERNAL_SERVER_ERROR_MESSAGE,
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
        .collect(Collectors.toList());
    return new ValidationResponse(validations);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ValidationResponse handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
    final List<Validation> validations = e.getBindingResult().getFieldErrors().stream()
        .map(error -> new Validation(error.getField(), error.getDefaultMessage()))
        .collect(Collectors.toList());
    return new ValidationResponse(validations);
  }

}
