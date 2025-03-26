package by.modsen.ratingservice.exception;

import by.modsen.ratingservice.client.exception.FeignClientException;
import by.modsen.ratingservice.constants.ApplicationConstants;
import by.modsen.ratingservice.dto.ExceptionDto;
import by.modsen.ratingservice.exception.converter.RatedByConversionException;
import by.modsen.ratingservice.exception.rating.InvalidRatedByUserException;
import by.modsen.ratingservice.exception.rating.InvalidUserIdException;
import by.modsen.ratingservice.exception.rating.RatingAlreadyExistsException;
import by.modsen.ratingservice.exception.rating.RatingNotFoundException;
import by.modsen.ratingservice.exception.security.AccessDeniedException;
import by.modsen.ratingservice.exception.validation.Validation;
import by.modsen.ratingservice.exception.validation.ValidationResponse;
import jakarta.validation.ConstraintViolationException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
        RatingAlreadyExistsException.class
    })
    @ResponseStatus(HttpStatus.CONFLICT)
    public ExceptionDto handleRatingAlreadyExistsException(MessageSourceException e) {
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
        RatingNotFoundException.class
    })
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ExceptionDto handleRatingNotFoundException(MessageSourceException e) {
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
        InvalidRatedByUserException.class,
        InvalidUserIdException.class
    })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionDto handleCustomBadRequestException(MessageSourceException e) {
        String message = messageSource.getMessage(
            e.getMessageKey(),
            e.getArgs(),
            LocaleContextHolder.getLocale()
        );

        return new ExceptionDto(
            message,
            HttpStatus.BAD_REQUEST,
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
        Exception.class,
        RatedByConversionException.class
    })
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ExceptionDto handleServerExceptions(Exception e) {
        log.error(e.getMessage(), e);

        return new ExceptionDto(
            ApplicationConstants.INTERNAL_SERVER_ERROR_MESSAGE,
            HttpStatus.INTERNAL_SERVER_ERROR,
            LocalDateTime.now()
        );
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
