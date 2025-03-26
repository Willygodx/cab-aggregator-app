package by.modsen.passengerservice.dto.response;

import java.util.UUID;

public record AverageRatingResponse(

    UUID userId,
    Double averageRating

) {
}
