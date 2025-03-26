package by.modsen.driverservice.dto.response;

import java.util.UUID;

public record AverageRatingResponse(

    UUID userId,
    Double averageRating

) {
}
