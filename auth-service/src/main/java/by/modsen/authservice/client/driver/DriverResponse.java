package by.modsen.ridesservice.client.driver;

import java.util.List;

public record DriverResponse(

    Long id,
    String firstName,
    String lastName,
    String email,
    String phoneNumber,
    String sex,
    Boolean isDeleted,
    List<Long> carIds

) {
}
