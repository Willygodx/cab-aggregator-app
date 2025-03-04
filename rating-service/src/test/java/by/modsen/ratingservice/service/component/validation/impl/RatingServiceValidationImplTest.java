package by.modsen.ratingservice.service.component.validation.impl;

import by.modsen.ratingservice.client.driver.DriverFeignClient;
import by.modsen.ratingservice.client.driver.DriverResponse;
import by.modsen.ratingservice.client.passenger.PassengerFeignClient;
import by.modsen.ratingservice.client.passenger.PassengerResponse;
import by.modsen.ratingservice.client.ride.RideFeignClient;
import by.modsen.ratingservice.client.ride.RideResponse;
import by.modsen.ratingservice.constants.RatingExceptionMessageKeys;
import by.modsen.ratingservice.constants.TestDataConstants;
import by.modsen.ratingservice.exception.rating.RatingAlreadyExistsException;
import by.modsen.ratingservice.exception.rating.RatingNotFoundException;
import by.modsen.ratingservice.model.Rating;
import by.modsen.ratingservice.model.enums.RatedBy;
import by.modsen.ratingservice.repository.RatingRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatCode;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RatingServiceValidationImplTest {

    @Mock
    private RatingRepository ratingRepository;

    @Mock
    private RideFeignClient rideFeignClient;

    @Mock
    private PassengerFeignClient passengerFeignClient;

    @Mock
    private DriverFeignClient driverFeignClient;

    @InjectMocks
    private RatingServiceValidationImpl ratingServiceValidation;

    @Test
    void checkRatingExists_NoException_WhenRatingDoesNotExist() {
        when(ratingRepository.existsByRideIdAndRatedBy(TestDataConstants.RIDE_ID, RatedBy.DRIVER)).thenReturn(false);

        assertThatCode(() -> ratingServiceValidation.checkRatingExists(TestDataConstants.RIDE_ID, RatedBy.DRIVER))
            .doesNotThrowAnyException();

        verify(ratingRepository).existsByRideIdAndRatedBy(TestDataConstants.RIDE_ID, RatedBy.DRIVER);
    }

    @Test
    void checkRatingExists_ThrowsException_WhenRatingExists() {
        when(ratingRepository.existsByRideIdAndRatedBy(TestDataConstants.RIDE_ID, RatedBy.DRIVER)).thenReturn(true);

        assertThatThrownBy(() -> ratingServiceValidation.checkRatingExists(TestDataConstants.RIDE_ID, RatedBy.DRIVER))
            .isInstanceOf(RatingAlreadyExistsException.class)
            .extracting("messageKey", "args")
            .containsExactly(RatingExceptionMessageKeys.RATING_ALREADY_EXISTS_MESSAGE, new Object[]{TestDataConstants.RIDE_ID, "driver"});

        verify(ratingRepository).existsByRideIdAndRatedBy(TestDataConstants.RIDE_ID, RatedBy.DRIVER);
    }

    @Test
    void checkRatingExistsById_NoException_WhenRatingExists() {
        when(ratingRepository.existsById(TestDataConstants.RATING_ID)).thenReturn(true);

        assertThatCode(() -> ratingServiceValidation.checkRatingExistsById(TestDataConstants.RATING_ID))
            .doesNotThrowAnyException();

        verify(ratingRepository).existsById(TestDataConstants.RATING_ID);
    }

    @Test
    void checkRatingExistsById_ThrowsException_WhenRatingNotFound() {
        when(ratingRepository.existsById(TestDataConstants.RATING_ID)).thenReturn(false);

        assertThatThrownBy(() -> ratingServiceValidation.checkRatingExistsById(TestDataConstants.RATING_ID))
            .isInstanceOf(RatingNotFoundException.class)
            .extracting("messageKey", "args")
            .containsExactly(RatingExceptionMessageKeys.RATING_NOT_FOUND_MESSAGE, new Object[]{TestDataConstants.RATING_ID});

        verify(ratingRepository).existsById(TestDataConstants.RATING_ID);
    }

    @Test
    void checkRatingExistsByPassengerId_NoException_WhenRatingExists() {
        when(ratingRepository.existsByPassengerId(TestDataConstants.PASSENGER_ID)).thenReturn(true);

        assertThatCode(() -> ratingServiceValidation.checkRatingExistsByPassengerId(TestDataConstants.PASSENGER_ID))
            .doesNotThrowAnyException();

        verify(ratingRepository).existsByPassengerId(TestDataConstants.PASSENGER_ID);
    }

    @Test
    void checkRatingExistsByPassengerId_ThrowsException_WhenRatingNotFound() {
        when(ratingRepository.existsByPassengerId(TestDataConstants.PASSENGER_ID)).thenReturn(false);

        assertThatThrownBy(() -> ratingServiceValidation.checkRatingExistsByPassengerId(TestDataConstants.PASSENGER_ID))
            .isInstanceOf(RatingNotFoundException.class)
            .extracting("messageKey", "args")
            .containsExactly(RatingExceptionMessageKeys.RATING_FROM_PASSENGER_NOT_FOUND_MESSAGE, new Object[]{TestDataConstants.PASSENGER_ID});

        verify(ratingRepository).existsByPassengerId(TestDataConstants.PASSENGER_ID);
    }

    @Test
    void checkRatingExistsByDriverId_NoException_WhenRatingExists() {
        when(ratingRepository.existsByDriverId(TestDataConstants.DRIVER_ID)).thenReturn(true);

        assertThatCode(() -> ratingServiceValidation.checkRatingExistsByDriverId(TestDataConstants.DRIVER_ID))
            .doesNotThrowAnyException();

        verify(ratingRepository).existsByDriverId(TestDataConstants.DRIVER_ID);
    }

    @Test
    void checkRatingExistsByDriverId_ThrowsException_WhenRatingNotFound() {
        when(ratingRepository.existsByDriverId(TestDataConstants.DRIVER_ID)).thenReturn(false);

        assertThatThrownBy(() -> ratingServiceValidation.checkRatingExistsByDriverId(TestDataConstants.DRIVER_ID))
            .isInstanceOf(RatingNotFoundException.class)
            .extracting("messageKey", "args")
            .containsExactly(RatingExceptionMessageKeys.RATING_FROM_DRIVER_NOT_FOUND_MESSAGE, new Object[]{TestDataConstants.DRIVER_ID});

        verify(ratingRepository).existsByDriverId(TestDataConstants.DRIVER_ID);
    }

    @Test
    void getRatingWithChecks_ReturnsRating_WhenRatingExists() {
        Rating rating = TestDataConstants.RATING;
        when(ratingRepository.findById(TestDataConstants.RATING_ID)).thenReturn(Optional.of(rating));

        Rating result = ratingServiceValidation.getRatingWithChecks(TestDataConstants.RATING_ID);

        assertThat(result).isEqualTo(rating);
        verify(ratingRepository).findById(TestDataConstants.RATING_ID);
    }

    @Test
    void getRatingWithChecks_ThrowsException_WhenRatingNotFound() {
        when(ratingRepository.findById(TestDataConstants.RATING_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> ratingServiceValidation.getRatingWithChecks(TestDataConstants.RATING_ID))
            .isInstanceOf(RatingNotFoundException.class)
            .extracting("messageKey", "args")
            .containsExactly(RatingExceptionMessageKeys.RATING_NOT_FOUND_MESSAGE, new Object[]{TestDataConstants.RATING_ID});

        verify(ratingRepository).findById(TestDataConstants.RATING_ID);
    }

    @Test
    void getRideWithChecks_ReturnsRideResponse_WhenRideExists() {
        RideResponse rideResponse = TestDataConstants.RIDE_RESPONSE;
        when(rideFeignClient.getRideById(TestDataConstants.RIDE_ID, TestDataConstants.LANGUAGE_TAG)).thenReturn(rideResponse);

        RideResponse result = ratingServiceValidation.getRideWithChecks(TestDataConstants.RIDE_ID, TestDataConstants.LANGUAGE_TAG);

        assertThat(result).isEqualTo(rideResponse);
        verify(rideFeignClient).getRideById(TestDataConstants.RIDE_ID, TestDataConstants.LANGUAGE_TAG);
    }

    @Test
    void checkPassengerExists_NoException_WhenPassengerExists() {
        PassengerResponse passengerResponse = TestDataConstants.PASSENGER_RESPONSE;
        when(passengerFeignClient.getPassengerById(TestDataConstants.PASSENGER_ID, TestDataConstants.LANGUAGE_TAG)).thenReturn(passengerResponse);

        assertThatCode(() -> ratingServiceValidation.checkPassengerExists(TestDataConstants.PASSENGER_ID, TestDataConstants.LANGUAGE_TAG))
            .doesNotThrowAnyException();

        verify(passengerFeignClient).getPassengerById(TestDataConstants.PASSENGER_ID, TestDataConstants.LANGUAGE_TAG);
    }

    @Test
    void checkDriverExists_NoException_WhenDriverExists() {
        DriverResponse driverResponse = TestDataConstants.DRIVER_RESPONSE;
        when(driverFeignClient.getDriverById(TestDataConstants.DRIVER_ID, TestDataConstants.LANGUAGE_TAG)).thenReturn(driverResponse);

        assertThatCode(() -> ratingServiceValidation.checkDriverExists(TestDataConstants.DRIVER_ID, TestDataConstants.LANGUAGE_TAG))
            .doesNotThrowAnyException();

        verify(driverFeignClient).getDriverById(TestDataConstants.DRIVER_ID, TestDataConstants.LANGUAGE_TAG);
    }
}