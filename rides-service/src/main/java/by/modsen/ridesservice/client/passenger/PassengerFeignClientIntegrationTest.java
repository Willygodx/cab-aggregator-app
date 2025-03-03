package by.modsen.ridesservice.client.passenger;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Profile;

@FeignClient(value = "passenger-service", url = "${passenger-driver-services.wire-mock.url}")
@Profile("test")
public interface PassengerFeignClientIntegrationTest extends PassengerFeignClient{
}
