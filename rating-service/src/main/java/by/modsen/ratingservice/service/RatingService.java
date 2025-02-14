package by.modsen.ratingservice.service;

import by.modsen.ratingservice.dto.request.RatingRequest;
import by.modsen.ratingservice.dto.response.AverageRatingResponse;
import by.modsen.ratingservice.dto.response.PageResponse;
import by.modsen.ratingservice.dto.response.RatingResponse;

public interface RatingService {

    RatingResponse createRating(RatingRequest ratingRequest, String languageTag);

    RatingResponse updateRating(RatingRequest ratingRequest, String ratingId);

    RatingResponse getRatingById(String ratingId);

    PageResponse<RatingResponse> getAllRatings(Integer offset, Integer limit);

    PageResponse<RatingResponse> getAllRatingsByPassenger(Integer offset, Integer limit, Long passengerId);

    PageResponse<RatingResponse> getAllRatingsByDriver(Integer offset, Integer limit, Long driverId);

    AverageRatingResponse getAverageRatingForPassenger(Long passengerId, String languageTag);

    AverageRatingResponse getAverageRatingForDriver(Long driverId, String languageTag);

    void deleteRating(String ratingId);

    void calculateAndSendAverageRatings();

}