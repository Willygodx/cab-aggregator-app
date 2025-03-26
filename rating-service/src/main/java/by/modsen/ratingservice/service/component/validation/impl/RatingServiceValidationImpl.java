package by.modsen.ratingservice.service.component.validation.impl;

import by.modsen.ratingservice.client.driver.DriverFeignClient;
import by.modsen.ratingservice.client.passenger.PassengerFeignClient;
import by.modsen.ratingservice.client.ride.RideFeignClient;
import by.modsen.ratingservice.client.ride.RideResponse;
import by.modsen.ratingservice.constants.ApplicationConstants;
import by.modsen.ratingservice.constants.RatingExceptionMessageKeys;
import by.modsen.ratingservice.exception.rating.RatingAlreadyExistsException;
import by.modsen.ratingservice.exception.rating.RatingNotFoundException;
import by.modsen.ratingservice.exception.security.AccessDeniedException;
import by.modsen.ratingservice.model.Rating;
import by.modsen.ratingservice.model.enums.RatedBy;
import by.modsen.ratingservice.repository.RatingRepository;
import by.modsen.ratingservice.service.component.validation.RatingServiceValidation;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
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
    public void checkRatingExistsByPassengerId(UUID passengerId) {
        if (!ratingRepository.existsByPassengerId(passengerId)) {
            throw new RatingNotFoundException(
              RatingExceptionMessageKeys.RATING_FROM_PASSENGER_NOT_FOUND_MESSAGE,
              passengerId
            );
        }
    }

    @Override
    public void checkRatingExistsByDriverId(UUID driverId) {
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
    public RideResponse getRideWithChecks(Long rideId, String languageTag, String authorization) {
        return rideFeignClient.getRideById(rideId, languageTag, authorization);
    }

    @Override
    public void checkPassengerExists(UUID passengerId, String languageTag, String authorization) {
        if (Objects.nonNull(passengerId)) {
            passengerFeignClient.getPassengerById(passengerId, languageTag, authorization);
        }
    }

    @Override
    public void checkDriverExists(UUID driverId, String languageTag, String authorization) {
        if (Objects.nonNull(driverId)) {
            driverFeignClient.getDriverById(driverId, languageTag, authorization);
        }
    }

    @Override
    public RatedBy validateUserRoleAndId(JwtAuthenticationToken token, Long rideId, String languageTag, String jwt) {
        Map<String, Object> realmAccess = token.getToken().getClaim(ApplicationConstants.REALM_ACCESS_CLAIM);
        List<String> roles = (List<String>) realmAccess.get(ApplicationConstants.ROLES_CLAIM);
        String userSub = token.getToken().getClaimAsString(ApplicationConstants.SUB_CLAIM);
        UUID userId = UUID.fromString(userSub);

        RideResponse rideResponse = getRideWithChecks(rideId, languageTag, jwt);
        UUID driverId = rideResponse.driverId();
        UUID passengerId = rideResponse.passengerId();

        if (roles.contains(ApplicationConstants.ROLE_VALUE_DRIVER)) {
            if (!userId.equals(driverId)) {
                throw new AccessDeniedException(RatingExceptionMessageKeys.DRIVER_ID_MISMATCH_MESSAGE);
            }
            return RatedBy.DRIVER;
        } else if (roles.contains(ApplicationConstants.ROLE_VALUE_PASSENGER)) {
            if (!userId.equals(passengerId)) {
                throw new AccessDeniedException(RatingExceptionMessageKeys.PASSENGER_ID_MISMATCH_MESSAGE);
            }
            return RatedBy.PASSENGER;
        } else {
            throw new AccessDeniedException(RatingExceptionMessageKeys.INVALID_ROLE_USER_MESSAGE);
        }
    }

    @Override
    public RideResponse getRideWithRoleChecks(Long rideId,
                                              String languageTag,
                                              String jwt,
                                              JwtAuthenticationToken token) {
        return getRideWithChecks(rideId, languageTag, jwt);
    }

}
