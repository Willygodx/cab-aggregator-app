package by.modsen.ratingservice.client.driver;

import java.util.UUID;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

public interface DriverFeignClient {

    @GetMapping("/api/v1/drivers/{driverId}")
    DriverResponse getDriverById(@PathVariable UUID driverId,
                                 @RequestHeader("Accept-Language") String acceptLanguage,
                                 @RequestHeader("Authorization") String authorization);

}
