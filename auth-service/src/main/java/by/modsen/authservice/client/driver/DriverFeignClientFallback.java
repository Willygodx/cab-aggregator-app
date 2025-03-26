package by.modsen.authservice.client.driver;

import by.modsen.authservice.client.exception.FeignClientException;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerErrorException;

@Component
public class DriverFeignClientFallback implements FallbackFactory<DriverFeignClient> {

    @Override
    public DriverFeignClient create(Throwable cause) {
        return (driverId, acceptLanguage, authorization) -> {
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
