package by.modsen.ratingservice.dto.response;

import java.util.UUID;

public record RatingResponse(

    String id,
    Long rideId,
    UUID driverId,
    UUID passengerId,
    Integer mark,
    String comment,
    String ratedBy

) {
}
