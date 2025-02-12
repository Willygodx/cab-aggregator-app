package by.modsen.ridesservice.service.component.validation.impl;

import by.modsen.ridesservice.client.driver.DriverFeignClient;
import by.modsen.ridesservice.client.passenger.PassengerFeignClient;
import by.modsen.ridesservice.constants.RideExceptionMessageKeys;
import by.modsen.ridesservice.dto.request.RideStatusRequest;
import by.modsen.ridesservice.exception.ride.RideNotFoundException;
import by.modsen.ridesservice.exception.ride.RideStatusIncorrectException;
import by.modsen.ridesservice.model.Ride;
import by.modsen.ridesservice.model.enums.RideStatus;
import by.modsen.ridesservice.repository.RideRepository;
import by.modsen.ridesservice.service.component.validation.RideServiceValidation;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.cglib.core.Local;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RideServiceValidationImpl implements RideServiceValidation {

    private final RideRepository rideRepository;
    private final PassengerFeignClient passengerFeignClient;
    private final DriverFeignClient driverFeignClient;

    public Ride findRideByIdWithCheck(Long rideId) {
        return rideRepository.findById(rideId)
            .orElseThrow(() -> new RideNotFoundException(
                RideExceptionMessageKeys.RIDE_NOT_FOUND_MESSAGE,
                rideId
            ));
    }

    public void checkRideExistsById(Long rideId) {
        if (!rideRepository.existsById(rideId)) {
            throw new RideNotFoundException(RideExceptionMessageKeys.RIDE_NOT_FOUND_MESSAGE, rideId);
        }
    }

    public void validChangeRideStatus(Ride ride, RideStatusRequest ridesStatusRequest) {
        RideStatus currentStatus = ride.getRideStatus();
        RideStatus newStatus = RideStatus.valueOf(ridesStatusRequest.rideStatus());

        if (currentStatus == RideStatus.DECLINED) {
            throw new RideStatusIncorrectException(RideExceptionMessageKeys.RIDE_STATUS_DECLINED_MESSAGE);
        }

        if (!currentStatus.canTransitionTo(newStatus)) {
            throw new RideStatusIncorrectException(
                RideExceptionMessageKeys.RIDE_STATUS_INCORRECT_MESSAGE,
                currentStatus,
                newStatus
            );
        }
    }

    public void checkPassengerExists(Long passengerId) {
        if (Objects.nonNull(passengerId)) {
            passengerFeignClient.getPassengerById(passengerId,
                                                  LocaleContextHolder.getLocale().toLanguageTag());
        }
    }

    public void checkDriverExists(Long driverId) {
        if (Objects.nonNull(driverId)) {
            driverFeignClient.getDriverById(driverId,
                                            LocaleContextHolder.getLocale().toLanguageTag());
        }
    }

}
