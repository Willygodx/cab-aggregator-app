package by.modsen.ratingservice.service.component.validation;

import by.modsen.ratingservice.model.Rating;
import by.modsen.ratingservice.model.enums.RatedBy;

public interface RatingServiceValidation {

    void checkRatingExists(Long rideId, RatedBy ratedBy);

    void checkRatingExistsById(String ratingId);

    Rating getRatingWithChecks(String ratingId);

    void checkRatingExistsByPassengerId(Long passengerId);

    void checkRatingExistsByDriverId(Long driverId);

}
