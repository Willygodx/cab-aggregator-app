package by.modsen.ridesservice.client.exception;

import by.modsen.ridesservice.dto.ExceptionDto;
import lombok.Getter;

@Getter
public class FeignClientException extends RuntimeException {

    private final ExceptionDto exceptionDto;

    public FeignClientException(ExceptionDto apiExceptionDto) {
        this.exceptionDto = apiExceptionDto;
    }

}
