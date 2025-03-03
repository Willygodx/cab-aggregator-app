package by.modsen.ratingservice.client.driver;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Profile;

@FeignClient(value = "driver-service", url = "${passenger-driver-services.wire-mock.url}")
@Profile("test")
public interface DriverFeignClientIntegrationTest extends DriverFeignClient {
}