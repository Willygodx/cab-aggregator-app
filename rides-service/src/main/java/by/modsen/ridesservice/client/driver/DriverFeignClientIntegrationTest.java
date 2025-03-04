package by.modsen.ridesservice.client.driver;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Profile;

@FeignClient(name = "driver-service", url = "${passenger-driver-services.wire-mock.url}")
@Profile("test")
public interface DriverFeignClientIntegrationTest extends DriverFeignClient {
}
