package by.modsen.authservice.exception.keycloak;

import by.modsen.authservice.dto.ExceptionDto;
import lombok.Getter;

@Getter
public class KeycloakUserException extends RuntimeException {

    private final ExceptionDto exceptionDto;

    public KeycloakUserException(ExceptionDto exceptionDto) {
        this.exceptionDto = exceptionDto;
    }

}
