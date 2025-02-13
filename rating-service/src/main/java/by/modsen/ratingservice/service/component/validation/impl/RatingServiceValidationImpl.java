package by.modsen.ratingservice.service.component.validation.impl;

import by.modsen.ratingservice.client.driver.DriverFeignClient;
import by.modsen.ratingservice.client.passenger.PassengerFeignClient;
import by.modsen.ratingservice.client.ride.RideFeignClient;
import by.modsen.ratingservice.client.ride.RideResponse;
import by.modsen.ratingservice.constants.RatingExceptionMessageKeys;
import by.modsen.ratingservice.exception.rating.RatingAlreadyExistsException;
import by.modsen.ratingservice.exception.rating.RatingNotFoundException;
import by.modsen.ratingservice.model.Rating;
import by.modsen.ratingservice.model.enums.RatedBy;
import by.modsen.ratingservice.repository.RatingRepository;
import by.modsen.ratingservice.service.component.validation.RatingServiceValidation;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RatingServiceValidationImpl implements RatingServiceValidation {

    private final RatingRepository ratingRepository;
    private final RideFeignClient rideFeignClient;
    private final PassengerFeignClient passengerFeignClient;
    private final DriverFeignClient driverFeignClient;

    @Override
    public void checkRatingExists(Long rideId, RatedBy ratedBy) {
        if (ratingRepository.existsByRideIdAndRatedBy(rideId, ratedBy)) {
            throw new RatingAlreadyExistsException(
                RatingExceptionMessageKeys.RATING_ALREADY_EXISTS_MESSAGE,
                rideId,
                ratedBy.name().toLowerCase()
            );
        }
    }

    @Override
    public void checkRatingExistsById(String ratingId) {
        if (!ratingRepository.existsById(ratingId)) {
            throw new RatingNotFoundException(
                RatingExceptionMessageKeys.RATING_NOT_FOUND_MESSAGE,
                ratingId
            );
        }
    }

    @Override
    public void checkRatingExistsByPassengerId(Long passengerId) {
        if (!ratingRepository.existsByPassengerId(passengerId)) {
            throw new RatingNotFoundException(
              RatingExceptionMessageKeys.RATING_FROM_PASSENGER_NOT_FOUND_MESSAGE,
              passengerId
            );
        }
    }

    @Override
    public void checkRatingExistsByDriverId(Long driverId) {
        if (!ratingRepository.existsByDriverId(driverId)) {
            throw new RatingNotFoundException(
              RatingExceptionMessageKeys.RATING_FROM_DRIVER_NOT_FOUND_MESSAGE,
              driverId
            );
        }
    }

    @Override
    public Rating getRatingWithChecks(String ratingId) {
        return ratingRepository.findById(ratingId)
            .orElseThrow(() -> new RatingNotFoundException(
                RatingExceptionMessageKeys.RATING_NOT_FOUND_MESSAGE,
                ratingId
            ));
    }

    @Override
    public RideResponse getRideWithChecks(Long rideId, String languageTag) {
        return rideFeignClient.getRideById(rideId, languageTag);
    }

    @Override
    public void checkPassengerExists(Long passengerId, String languageTag) {
        if (Objects.nonNull(passengerId)) {
            passengerFeignClient.getPassengerById(passengerId, languageTag);
        }
    }

    @Override
    public void checkDriverExists(Long driverId, String languageTag) {
        if (Objects.nonNull(driverId)) {
            driverFeignClient.getDriverById(driverId, languageTag);
        }
    }

}
