package by.modsen.ratingservice.client.ride;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

public interface RideFeignClient {

    @GetMapping("/api/v1/rides/{rideId}")
    RideResponse getRideById(@PathVariable Long rideId,
                             @RequestHeader("Accept-Language") String acceptLanguage);

}
