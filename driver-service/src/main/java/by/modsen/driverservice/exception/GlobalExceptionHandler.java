package by.modsen.driverservice.exception;

import by.modsen.driverservice.constants.ApplicationExceptionMessages;
import by.modsen.driverservice.dto.ExceptionDto;
import by.modsen.driverservice.exception.car.CarNotFoundException;
import by.modsen.driverservice.exception.car.CarNumberAlreadyExistsException;
import by.modsen.driverservice.exception.driver.DriverAlreadyExistsException;
import by.modsen.driverservice.exception.driver.DriverNotFoundException;
import by.modsen.driverservice.exception.validation.Validation;
import by.modsen.driverservice.exception.validation.ValidationResponse;
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

  @ExceptionHandler(
      {
          CarNumberAlreadyExistsException.class,
          DriverAlreadyExistsException.class
      }
  )
  @ResponseStatus(HttpStatus.CONFLICT)
  public ExceptionDto handleConflictExceptions(Exception e) {
    return new ExceptionDto(e.getMessage(), HttpStatus.CONFLICT, LocalDateTime.now());
  }

  @ExceptionHandler(
      {
          CarNotFoundException.class,
          DriverNotFoundException.class
      }
  )
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public ExceptionDto handleNotFoundExceptions(Exception e) {
    return new ExceptionDto(e.getMessage(), HttpStatus.NOT_FOUND, LocalDateTime.now());
  }

  @ExceptionHandler(Exception.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public ExceptionDto handleServerErrors() {
    return new ExceptionDto(
        ApplicationExceptionMessages.SERVER_ERROR_MESSAGE,
        HttpStatus.INTERNAL_SERVER_ERROR,
        LocalDateTime.now());
  }

  @ExceptionHandler({ConstraintViolationException.class})
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
