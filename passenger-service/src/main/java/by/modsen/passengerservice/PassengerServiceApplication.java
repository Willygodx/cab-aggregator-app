package by.modsen.passengerservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

//@EnableDiscoveryClient
@SpringBootApplication
public class PassengerServiceApplication {

  public static void main(String[] args) {
    SpringApplication.run(PassengerServiceApplication.class, args);
  }

}
