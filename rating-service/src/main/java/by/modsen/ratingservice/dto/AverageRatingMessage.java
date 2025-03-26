package by.modsen.ratingservice.dto;

import java.util.UUID;

public record AverageRatingMessage(

    UUID userId,
    Double averageRating

) {
}
