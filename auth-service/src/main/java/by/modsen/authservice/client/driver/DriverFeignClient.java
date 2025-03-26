package by.modsen.authservice.client.driver;

import by.modsen.authservice.dto.request.DriverRequestWithKeycloakId;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

public interface DriverFeignClient {

    @PostMapping("api/v1/drivers")
    DriverResponse createDriver(@RequestBody DriverRequestWithKeycloakId request,
                                @RequestHeader("Accept-Language") String acceptLanguage,
                                @RequestHeader("Authorization") String authorization);

}
