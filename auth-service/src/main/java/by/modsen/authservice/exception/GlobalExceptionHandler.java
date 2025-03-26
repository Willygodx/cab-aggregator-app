package by.modsen.authservice.exception;

import by.modsen.authservice.client.exception.FeignClientException;
import by.modsen.authservice.constants.ApplicationConstants;
import by.modsen.authservice.dto.ExceptionDto;
import by.modsen.authservice.exception.keycloak.KeycloakException;
import by.modsen.authservice.exception.keycloak.KeycloakUserException;
import by.modsen.authservice.exception.validation.Validation;
import by.modsen.authservice.exception.validation.ValidationResponse;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.ServiceUnavailableException;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class GlobalExceptionHandler {

    private final MessageSource messageSource;

    @ExceptionHandler({
        KeycloakUserException.class
    })
    public ResponseEntity<ExceptionDto> handleKeycloakUserException(KeycloakUserException e) {
        return ResponseEntity.status(
                e.getExceptionDto()
                    .status())
            .body(e.getExceptionDto());
    }

    @ExceptionHandler({
        KeycloakException.class
    })
    public ResponseEntity<ExceptionDto> handleKeycloakException(KeycloakException e) {
        return ResponseEntity.status(
                e.getExceptionDto()
                    .status())
            .body(e.getExceptionDto());
    }

    @ExceptionHandler({
        FeignClientException.class
    })
    public ResponseEntity<ExceptionDto> handleFeignClientException(FeignClientException e) {
        return ResponseEntity.status(
                e.getExceptionDto()
                    .status())
            .body(e.getExceptionDto());
    }

    @ExceptionHandler({
        ServiceUnavailableException.class
    })
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    public ExceptionDto handleServiceUnavailableException(MessageSourceException e) {
        String message = messageSource.getMessage(
            e.getMessage(),
            e.getArgs(),
            LocaleContextHolder.getLocale()
        );

        return new ExceptionDto(
            message,
            HttpStatus.SERVICE_UNAVAILABLE,
            LocalDateTime.now()
        );
    }

    @ExceptionHandler({
        Exception.class
    })
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ExceptionDto handleServerErrors(Exception e) {
        log.error(e.getMessage(), e);

        return new ExceptionDto(
            ApplicationConstants.INTERNAL_SERVER_ERROR_MESSAGE,
            HttpStatus.INTERNAL_SERVER_ERROR,
            LocalDateTime.now()
        );
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
