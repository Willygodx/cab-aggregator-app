package by.modsen.driverservice.configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;

@OpenAPIDefinition(
    info = @Info(
        title = "Driver Service API",
        description = "Service provides API for actions connected to " +
            "drivers. One of the services from the cab aggregator app.", version = "1.0.0",
        contact = @Contact(
            name = "Ruslan Alhava",
            email = "linkong344@gmail.com"
        )
    )
)
public class SwaggerConfig {
}
