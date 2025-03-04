package by.modsen.ridesservice.service.component.validation.impl;

import by.modsen.ridesservice.client.driver.DriverFeignClient;
import by.modsen.ridesservice.client.driver.DriverResponse;
import by.modsen.ridesservice.client.passenger.PassengerFeignClient;
import by.modsen.ridesservice.client.passenger.PassengerResponse;
import by.modsen.ridesservice.constants.RideExceptionMessageKeys;
import by.modsen.ridesservice.constants.TestDataConstants;
import by.modsen.ridesservice.dto.request.RideStatusRequest;
import by.modsen.ridesservice.exception.ride.RideNotFoundException;
import by.modsen.ridesservice.exception.ride.RideStatusIncorrectException;
import by.modsen.ridesservice.model.Ride;
import by.modsen.ridesservice.model.enums.RideStatus;
import by.modsen.ridesservice.repository.RideRepository;
import java.util.List;
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
class RideServiceValidationImplTest {

    @Mock
    private RideRepository rideRepository;

    @Mock
    private PassengerFeignClient passengerFeignClient;

    @Mock
    private DriverFeignClient driverFeignClient;

    @InjectMocks
    private RideServiceValidationImpl rideServiceValidation;

    @Test
    void findRideByIdWithCheck_ReturnsRide_WhenRideExists() {
        when(rideRepository.findById(TestDataConstants.RIDE_ID)).thenReturn(Optional.of(TestDataConstants.RIDE));

        Ride result = rideServiceValidation.findRideByIdWithCheck(TestDataConstants.RIDE_ID);

        assertThat(result).isEqualTo(TestDataConstants.RIDE);
        verify(rideRepository).findById(TestDataConstants.RIDE_ID);
    }

    @Test
    void findRideByIdWithCheck_ThrowsException_WhenRideNotFound() {
        when(rideRepository.findById(TestDataConstants.RIDE_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> rideServiceValidation.findRideByIdWithCheck(TestDataConstants.RIDE_ID))
            .isInstanceOf(RideNotFoundException.class)
            .extracting("messageKey", "args")
            .containsExactly(RideExceptionMessageKeys.RIDE_NOT_FOUND_MESSAGE, new Object[]{TestDataConstants.RIDE_ID});

        verify(rideRepository).findById(TestDataConstants.RIDE_ID);
    }

    @Test
    void checkRideExistsById_NoException_WhenRideExists() {
        when(rideRepository.existsById(TestDataConstants.RIDE_ID)).thenReturn(true);

        assertThatCode(() -> rideServiceValidation.checkRideExistsById(TestDataConstants.RIDE_ID))
            .doesNotThrowAnyException();

        verify(rideRepository).existsById(TestDataConstants.RIDE_ID);
    }

    @Test
    void checkRideExistsById_ThrowsException_WhenRideNotFound() {
        when(rideRepository.existsById(TestDataConstants.RIDE_ID)).thenReturn(false);

        assertThatThrownBy(() -> rideServiceValidation.checkRideExistsById(TestDataConstants.RIDE_ID))
            .isInstanceOf(RideNotFoundException.class)
            .extracting("messageKey", "args")
            .containsExactly(RideExceptionMessageKeys.RIDE_NOT_FOUND_MESSAGE, new Object[]{TestDataConstants.RIDE_ID});

        verify(rideRepository).existsById(TestDataConstants.RIDE_ID);
    }

    @Test
    void validChangeRideStatus_NoException_WhenStatusTransitionIsValid() {
        Ride ride = TestDataConstants.RIDE;
        ride.setRideStatus(RideStatus.CREATED);

        RideStatusRequest rideStatusRequest = new RideStatusRequest(TestDataConstants.RIDE_STATUS_ACCEPTED);

        assertThatCode(() -> rideServiceValidation.validChangeRideStatus(ride, rideStatusRequest))
            .doesNotThrowAnyException();
    }

    @Test
    void validChangeRideStatus_ThrowsException_WhenRideIsDeclined() {
        Ride ride = TestDataConstants.RIDE;
        ride.setRideStatus(RideStatus.DECLINED);

        RideStatusRequest rideStatusRequest = new RideStatusRequest(TestDataConstants.RIDE_STATUS_ACCEPTED);

        assertThatThrownBy(() -> rideServiceValidation.validChangeRideStatus(ride, rideStatusRequest))
            .isInstanceOf(RideStatusIncorrectException.class)
            .extracting("messageKey")
            .isEqualTo(RideExceptionMessageKeys.RIDE_STATUS_DECLINED_MESSAGE);
    }

    @Test
    void validChangeRideStatus_ThrowsException_WhenStatusTransitionIsInvalid() {
        Ride ride = TestDataConstants.RIDE;
        ride.setRideStatus(RideStatus.CREATED);

        RideStatusRequest rideStatusRequest = new RideStatusRequest(TestDataConstants.RIDE_STATUS_FINISHED);

        assertThatThrownBy(() -> rideServiceValidation.validChangeRideStatus(ride, rideStatusRequest))
            .isInstanceOf(RideStatusIncorrectException.class)
            .extracting("messageKey", "args")
            .containsExactly(RideExceptionMessageKeys.RIDE_STATUS_INCORRECT_MESSAGE, new Object[]{RideStatus.CREATED, RideStatus.FINISHED});
    }

    @Test
    void checkPassengerExists_NoException_WhenPassengerExists() {
        PassengerResponse passengerResponse = TestDataConstants.PASSENGER_RESPONSE;

        when(passengerFeignClient.getPassengerById(TestDataConstants.PASSENGER_ID, TestDataConstants.LANGUAGE_TAG))
            .thenReturn(passengerResponse);

        assertThatCode(() -> rideServiceValidation.checkPassengerExists(
            TestDataConstants.PASSENGER_ID,
            TestDataConstants.LANGUAGE_TAG
        ))
            .doesNotThrowAnyException();

        verify(passengerFeignClient).getPassengerById(TestDataConstants.PASSENGER_ID, TestDataConstants.LANGUAGE_TAG);
    }

    @Test
    void checkDriverExists_NoException_WhenDriverExists() {
        DriverResponse driverResponse = TestDataConstants.DRIVER_RESPONSE;

        when(driverFeignClient.getDriverById(TestDataConstants.DRIVER_ID, TestDataConstants.LANGUAGE_TAG))
            .thenReturn(driverResponse);

        assertThatCode(() -> rideServiceValidation.checkDriverExists(
            TestDataConstants.DRIVER_ID,
            TestDataConstants.LANGUAGE_TAG
        ))
            .doesNotThrowAnyException();

        verify(driverFeignClient).getDriverById(TestDataConstants.DRIVER_ID, TestDataConstants.LANGUAGE_TAG);
    }

}