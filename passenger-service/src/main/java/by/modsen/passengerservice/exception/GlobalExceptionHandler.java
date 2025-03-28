package by.modsen.passengerservice.exception;

import by.modsen.passengerservice.constants.ApplicationExceptionMessages;
import by.modsen.passengerservice.dto.ExceptionDto;
import by.modsen.passengerservice.exception.passenger.PassengerAlreadyExistsException;
import by.modsen.passengerservice.exception.passenger.PassengerNotFoundException;
import by.modsen.passengerservice.exception.security.AccessDeniedException;
import by.modsen.passengerservice.exception.validation.Validation;
import by.modsen.passengerservice.exception.validation.ValidationResponse;
import jakarta.validation.ConstraintViolationException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class GlobalExceptionHandler {

    private final MessageSource messageSource;

    @ExceptionHandler({
        PassengerAlreadyExistsException.class
    })
    @ResponseStatus(HttpStatus.CONFLICT)
    public ExceptionDto handlePassengerAlreadyExistsException(MessageSourceException e) {
        String message = messageSource.getMessage(
            e.getMessageKey(),
            e.getArgs(),
            LocaleContextHolder.getLocale()
        );

        return new ExceptionDto(
            message,
            HttpStatus.CONFLICT,
            LocalDateTime.now()
        );
    }

    @ExceptionHandler({
        PassengerNotFoundException.class
    })
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ExceptionDto handlePassengerNotFoundException(MessageSourceException e) {
        String message = messageSource.getMessage(
            e.getMessageKey(),
            e.getArgs(),
            LocaleContextHolder.getLocale()
        );

        return new ExceptionDto(
            message,
            HttpStatus.NOT_FOUND,
            LocalDateTime.now()
        );
    }

    @ExceptionHandler({
        Exception.class
    })
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ExceptionDto handleInternalServerErrors(Exception e) {
        log.error(e.getMessage(), e);

        return new ExceptionDto(
            ApplicationExceptionMessages.INTERNAL_SERVER_ERROR_MESSAGE,
            HttpStatus.INTERNAL_SERVER_ERROR,
            LocalDateTime.now()
        );
    }

    @ExceptionHandler({
        AuthorizationDeniedException.class
    })
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ExceptionDto handleAuthorizationDeniedException(Exception e) {
        return new ExceptionDto(
            e.getMessage(),
            HttpStatus.FORBIDDEN,
            LocalDateTime.now()
        );
    }

    @ExceptionHandler({
        AccessDeniedException.class
    })
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ExceptionDto handleAccessDeniedException(MessageSourceException e) {
        String message = messageSource.getMessage(
            e.getMessageKey(),
            e.getArgs(),
            LocaleContextHolder.getLocale()
        );

        return new ExceptionDto(
            message,
            HttpStatus.FORBIDDEN,
            LocalDateTime.now()
        );
    }

    @ExceptionHandler({
        ConstraintViolationException.class
    })
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

    @ExceptionHandler({
        MethodArgumentNotValidException.class
    })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ValidationResponse handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        final List<Validation> validations = e.getBindingResult().getFieldErrors().stream()
            .map(error -> new Validation(error.getField(), error.getDefaultMessage()))
            .collect(Collectors.toList());
        return new ValidationResponse(validations);
    }

}
