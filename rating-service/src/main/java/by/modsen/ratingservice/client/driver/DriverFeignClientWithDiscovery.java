package by.modsen.ratingservice.client.driver;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Profile;

@FeignClient(value = "driver-service")
@Profile("dev")
public interface DriverFeignClientWithDiscovery extends DriverFeignClient {
}
