package by.modsen.ratingservice.client.exception;

import by.modsen.ratingservice.dto.ExceptionDto;
import lombok.Getter;

@Getter
public class FeignClientException extends RuntimeException {

    private final ExceptionDto exceptionDto;

    public FeignClientException(ExceptionDto apiExceptionDto) {
        this.exceptionDto = apiExceptionDto;
    }

}
