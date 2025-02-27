package by.modsen.ridesservice.client.driver;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

public interface DriverFeignClient {

    @GetMapping("/api/v1/drivers/{driverId}")
    DriverResponse getDriverById(@PathVariable Long driverId,
                                 @RequestHeader("Accept-Language") String acceptLanguage);

}
