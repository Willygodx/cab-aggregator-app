package by.modsen.driverservice.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import java.util.UUID;

@Schema(description = "Data Transfer Object for Driver")
public record DriverResponse(

    UUID id,
    String firstName,
    String lastName,
    String email,
    String phoneNumber,
    String sex,
    Double averageRating,
    Boolean isDeleted,
    List<Long> carIds

) {
}
