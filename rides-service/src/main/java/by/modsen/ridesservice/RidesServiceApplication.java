package by.modsen.ridesservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class RidesServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(RidesServiceApplication.class, args);
    }

}
