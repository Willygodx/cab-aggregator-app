package by.modsen.authservice.exception.keycloak;

import by.modsen.authservice.dto.ExceptionDto;
import lombok.Getter;

@Getter
public class KeycloakException extends RuntimeException {

    private final ExceptionDto exceptionDto;

    public KeycloakException(ExceptionDto exceptionDto) {
        this.exceptionDto = exceptionDto;
    }

}
