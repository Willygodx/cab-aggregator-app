package by.modsen.ridesservice.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record RideResponse(

    Long id,
    Long driverId,
    Long passengerId,
    String pickupAddress,
    String destinationAddress,
    String rideStatus,
    LocalDateTime orderDateTime,
    BigDecimal cost

) {
}
