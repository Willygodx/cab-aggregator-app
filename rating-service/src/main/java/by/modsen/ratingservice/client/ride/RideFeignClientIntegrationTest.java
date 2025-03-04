package by.modsen.ratingservice.client.ride;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Profile;

@FeignClient(value = "ride-service", url = "${passenger-driver-services.wire-mock.url}")
@Profile("test")
public interface RideFeignClientIntegrationTest extends RideFeignClient {
}
