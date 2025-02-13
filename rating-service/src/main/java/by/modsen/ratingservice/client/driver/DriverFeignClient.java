package by.modsen.ratingservice.client.driver;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(value = "driver-service")
public interface DriverFeignClient {

    @GetMapping("/api/v1/drivers/{driverId}")
    DriverResponse getDriverById(@PathVariable Long driverId,
                                 @RequestHeader("Accept-Language") String acceptLanguage);

}
