package by.modsen.ratingservice.service.impl;

import by.modsen.ratingservice.client.ride.RideResponse;
import by.modsen.ratingservice.dto.request.RatingRequest;
import by.modsen.ratingservice.dto.response.AverageRatingResponse;
import by.modsen.ratingservice.dto.response.PageResponse;
import by.modsen.ratingservice.dto.response.RatingResponse;
import by.modsen.ratingservice.mapper.PageResponseMapper;
import by.modsen.ratingservice.mapper.RatingMapper;
import by.modsen.ratingservice.model.Rating;
import by.modsen.ratingservice.model.enums.RatedBy;
import by.modsen.ratingservice.repository.RatingRepository;
import by.modsen.ratingservice.service.RatingService;
import by.modsen.ratingservice.service.component.validation.RatingServiceValidation;
import java.math.BigDecimal;
import java.math.RoundingMode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RatingServiceImpl implements RatingService {

    private final RatingMapper ratingMapper;
    private final PageResponseMapper pageResponseMapper;
    private final RatingServiceValidation ratingServiceValidation;
    private final RatingRepository ratingRepository;

    @Override
    public RatingResponse createRating(RatingRequest ratingRequest, String languageTag) {
        Long rideId = ratingRequest.rideId();
        String ratedBy = ratingRequest.ratedBy();

        ratingServiceValidation.checkRatingExists(rideId, RatedBy.valueOf(ratedBy));

        RideResponse rideResponse = ratingServiceValidation.getRideWithChecks(rideId, languageTag);
        Long driverId = rideResponse.driverId();
        Long passengerId = rideResponse.passengerId();

        Rating rating = ratingMapper.toEntity(ratingRequest,
                                              driverId,
                                              passengerId,
                                              RatedBy.valueOf(ratedBy));

        rating = ratingRepository.save(rating);

        return ratingMapper.toResponse(rating);
    }

    @Override
    public RatingResponse updateRating(RatingRequest ratingRequest, String ratingId) {
        Rating rating = ratingServiceValidation.getRatingWithChecks(ratingId);

        ratingMapper.updateRatingFromDto(ratingRequest, rating);

        rating = ratingRepository.save(rating);

        return ratingMapper.toResponse(rating);
    }

    @Override
    public RatingResponse getRatingById(String ratingId) {
        Rating rating = ratingServiceValidation.getRatingWithChecks(ratingId);

        return ratingMapper.toResponse(rating);
    }

    @Override
    public PageResponse<RatingResponse> getAllRatings(Integer offset, Integer limit) {
        Page<RatingResponse> ratingDtoPage = ratingRepository
            .findAll(PageRequest.of(offset, limit))
            .map(ratingMapper::toResponse);

        return pageResponseMapper.toDto(ratingDtoPage);
    }

    @Override
    public PageResponse<RatingResponse> getAllRatingsByPassenger(Integer offset, Integer limit, Long passengerId) {
        ratingServiceValidation.checkRatingExistsByPassengerId(passengerId);

        Page<RatingResponse> ratingDtoPage = ratingRepository
            .findAllByPassengerIdAndRatedBy(passengerId, RatedBy.PASSENGER, PageRequest.of(offset, limit))
            .map(ratingMapper::toResponse);

        return pageResponseMapper.toDto(ratingDtoPage);
    }

    @Override
    public PageResponse<RatingResponse> getAllRatingsByDriver(Integer offset, Integer limit, Long driverId) {
        ratingServiceValidation.checkRatingExistsByDriverId(driverId);

        Page<RatingResponse> ratingDtoPage = ratingRepository
            .findAllByDriverIdAndRatedBy(driverId, RatedBy.DRIVER, PageRequest.of(offset, limit))
            .map(ratingMapper::toResponse);

        return pageResponseMapper.toDto(ratingDtoPage);
    }

    @Override
    public AverageRatingResponse getAverageRatingForPassenger(Long passengerId, String languageTag) {
        ratingServiceValidation.checkPassengerExists(passengerId, languageTag);

        Double averageRating = ratingRepository
            .findAllByPassengerIdAndRatedBy(passengerId, RatedBy.DRIVER)
            .stream()
            .mapToInt(Rating::getMark)
            .average()
            .orElse(0.0);

        return new AverageRatingResponse(roundToTwoDecimalPlaces(averageRating));
    }

    @Override
    public AverageRatingResponse getAverageRatingForDriver(Long driverId, String languageTag) {
        ratingServiceValidation.checkDriverExists(driverId, languageTag);

        Double averageRating = ratingRepository
            .findAllByDriverIdAndRatedBy(driverId, RatedBy.PASSENGER)
            .stream()
            .mapToInt(Rating::getMark)
            .average()
            .orElse(0.0);

        return new AverageRatingResponse(roundToTwoDecimalPlaces(averageRating));
    }

    private double roundToTwoDecimalPlaces(Double value) {
        return new BigDecimal(value)
            .setScale(2, RoundingMode.HALF_UP)
            .doubleValue();
    }

    @Override
    public void deleteRating(String ratingId) {
        ratingServiceValidation.checkRatingExistsById(ratingId);

        ratingRepository.deleteById(ratingId);
    }

}
