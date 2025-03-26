package by.modsen.ratingservice.client.ride;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record RideResponse(

    Long id,
    UUID driverId,
    UUID passengerId,
    String pickupAddress,
    String destinationAddress,
    String rideStatus,
    LocalDateTime orderDateTime,
    BigDecimal cost

) {
}
