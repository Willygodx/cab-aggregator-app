package by.modsen.ratingservice.service;

import by.modsen.ratingservice.dto.request.RatingRequest;
import by.modsen.ratingservice.dto.response.AverageRatingResponse;
import by.modsen.ratingservice.dto.response.PageResponse;
import by.modsen.ratingservice.dto.response.RatingResponse;
import java.util.UUID;

public interface RatingService {

    RatingResponse createRating(RatingRequest ratingRequest, String languageTag);

    RatingResponse updateRating(RatingRequest ratingRequest, String ratingId);

    RatingResponse getRatingById(String ratingId);

    PageResponse<RatingResponse> getAllRatings(Integer offset, Integer limit);

    PageResponse<RatingResponse> getAllRatingsByPassenger(Integer offset, Integer limit, UUID passengerId);

    PageResponse<RatingResponse> getAllRatingsByDriver(Integer offset, Integer limit, UUID driverId);

    AverageRatingResponse getAverageRatingForPassenger(UUID passengerId, String languageTag);

    AverageRatingResponse getAverageRatingForDriver(UUID driverId, String languageTag);

    void deleteRating(String ratingId);

    void calculateAndSendAverageRatings();

}