package by.modsen.ridesservice.client.driver;

import java.util.List;
import java.util.UUID;

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
