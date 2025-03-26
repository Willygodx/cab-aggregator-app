package by.modsen.authservice.client.passenger;

import by.modsen.authservice.dto.request.PassengerRequestWithKeycloakId;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

public interface PassengerFeignClient {

    @PostMapping("api/v1/passengers")
    PassengerResponse createPassenger(@RequestBody PassengerRequestWithKeycloakId request,
                                      @RequestHeader("Accept-Language") String acceptLanguage,
                                      @RequestHeader("Authorization") String authorization);

}