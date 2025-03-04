package by.modsen.ratingservice.client.ride;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Profile;

@FeignClient(value = "ride-service")
@Profile("dev")
public interface RideFeignClientWithDiscovery extends RideFeignClient {
}
