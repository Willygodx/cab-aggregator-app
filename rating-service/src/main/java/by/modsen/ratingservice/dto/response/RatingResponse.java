package by.modsen.ratingservice.dto.response;

public record RatingResponse(

    String id,
    Long rideId,
    Long driverId,
    Long passengerId,
    Integer mark,
    String comment,
    String ratedBy

) {
}
