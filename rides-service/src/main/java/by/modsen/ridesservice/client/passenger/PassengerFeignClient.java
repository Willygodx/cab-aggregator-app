package by.modsen.ridesservice.client.passenger;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

public interface PassengerFeignClient {

    @GetMapping("api/v1/passengers/{passengerId}")
    PassengerResponse getPassengerById(@PathVariable Long passengerId,
                                       @RequestHeader("Accept-Language") String acceptLanguage);

}