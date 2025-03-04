package by.modsen.ridesservice.client.passenger;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Profile;

@FeignClient(value = "passenger-service")
@Profile("dev")
public interface PassengerFeignClientWithDiscovery extends PassengerFeignClient {
}
