package by.modsen.authservice.client.exception;

import by.modsen.authservice.dto.ExceptionDto;
import lombok.Getter;

@Getter
public class FeignClientException extends RuntimeException {

    private final ExceptionDto exceptionDto;

    public FeignClientException(ExceptionDto exceptionDto) {
        this.exceptionDto = exceptionDto;
    }

}
