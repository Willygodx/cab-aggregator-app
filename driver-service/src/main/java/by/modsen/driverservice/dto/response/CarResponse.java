package by.modsen.driverservice.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "Data Transfer Object for Car")
public record CarResponse(

    Long id,
    String color,
    String carBrand,
    String carNumber,
    Boolean isDeleted,
    List<Long> driverIds

) {
}

