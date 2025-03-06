package by.modsen.ratingservice.client.passenger;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Profile;

@FeignClient(value = "passenger-service", fallbackFactory = PassengerFeignClientFallback.class)
@Profile("dev")
public interface PassengerFeignClientWithDiscovery extends PassengerFeignClient {
}
