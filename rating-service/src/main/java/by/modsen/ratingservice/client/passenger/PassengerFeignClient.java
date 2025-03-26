package by.modsen.ratingservice.client.passenger;

import java.util.UUID;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

public interface PassengerFeignClient {

    @GetMapping("api/v1/passengers/{passengerId}")
    PassengerResponse getPassengerById(@PathVariable UUID passengerId,
                                       @RequestHeader("Accept-Language") String acceptLanguage,
                                       @RequestHeader("Authorization") String authorization);

}