package by.modsen.driverservice.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Data Transfer Object for Driver")
public record DriverResponse(

    Long id,
    String firstName,
    String lastName,
    String email,
    String phoneNumber,
    String sex,
    Boolean isDeleted

) {
}
