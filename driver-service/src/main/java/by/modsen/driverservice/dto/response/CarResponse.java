package by.modsen.driverservice.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Data Transfer Object for Car")
public record CarResponse(

    Long id,
    String color,
    String carBrand,
    String carNumber,
    Boolean isDeleted

) {
}

