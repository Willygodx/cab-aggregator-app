package by.modsen.ratingservice.service.component.validation;

import by.modsen.ratingservice.client.ride.RideResponse;
import by.modsen.ratingservice.model.Rating;
import by.modsen.ratingservice.model.enums.RatedBy;
import java.util.UUID;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

public interface RatingServiceValidation {

    void checkRatingExists(Long rideId, RatedBy ratedBy);

    void checkRatingExistsById(String ratingId);

    Rating getRatingWithChecks(String ratingId);

    void checkRatingExistsByPassengerId(UUID passengerId);

    void checkRatingExistsByDriverId(UUID driverId);

    RideResponse getRideWithChecks(Long rideId, String languageTag, String authorization);

    void checkPassengerExists(UUID passengerId, String languageTag, String authorization);

    void checkDriverExists(UUID driverId, String languageTag, String authorization);

    RatedBy validateUserRoleAndId(JwtAuthenticationToken token, Long rideId, String languageTag, String jwt);

    RideResponse getRideWithRoleChecks(Long rideId, String languageTag, String jwt, JwtAuthenticationToken token);

}
