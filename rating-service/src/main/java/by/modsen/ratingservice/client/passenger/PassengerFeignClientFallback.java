package by.modsen.ratingservice.client.passenger;

import by.modsen.ratingservice.client.exception.FeignClientException;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerErrorException;

@Component
public class PassengerFeignClientFallback implements FallbackFactory<PassengerFeignClient> {

    @Override
    public PassengerFeignClient create(Throwable cause) {
        return (passengerId, acceptLanguage) -> {
            Throwable rootCause = getRootCause(cause);
            if (rootCause instanceof FeignClientException feignClientException) {
                throw feignClientException;
            }
            throw new ServerErrorException(cause.getMessage(), cause);
        };
    }

    private Throwable getRootCause(Throwable throwable) {
        Throwable cause = null;
        Throwable result = throwable;

        while (null != (cause = result.getCause())) {
            result = cause;
        }
        return result;
    }

}

