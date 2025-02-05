package by.modsen.ridesservice.service.component.validation.impl;

import by.modsen.ridesservice.constants.RideExceptionMessageKeys;
import by.modsen.ridesservice.dto.request.RideStatusRequest;
import by.modsen.ridesservice.exception.ride.RideNotFoundException;
import by.modsen.ridesservice.exception.ride.RideStatusIncorrectException;
import by.modsen.ridesservice.model.Ride;
import by.modsen.ridesservice.model.enums.RideStatus;
import by.modsen.ridesservice.repository.RideRepository;
import by.modsen.ridesservice.service.component.validation.RideServiceValidation;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RideServiceValidationImpl implements RideServiceValidation {

    private final MessageSource messageSource;
    private final RideRepository rideRepository;

    public Ride findRideByIdWithCheck(Long rideId) {
        return rideRepository.findById(rideId)
            .orElseThrow(() -> new RideNotFoundException(messageSource.getMessage(
                RideExceptionMessageKeys.RIDE_NOT_FOUND_MESSAGE,
                new Object[] {rideId},
                LocaleContextHolder.getLocale()
            )));
    }

    public void checkRideExistsById(Long rideId) {
        if (!rideRepository.existsById(rideId)) {
            throw new RideNotFoundException(messageSource.getMessage(
                RideExceptionMessageKeys.RIDE_NOT_FOUND_MESSAGE,
                new Object[] {rideId},
                LocaleContextHolder.getLocale()
            ));
        }
    }

    public void validChangeRideStatus(Ride ride, RideStatusRequest ridesStatusRequest) {
        RideStatus currentStatus = ride.getRideStatus();
        RideStatus newStatus = RideStatus.valueOf(ridesStatusRequest.rideStatus());

        if (currentStatus == RideStatus.DECLINED) {
            throw new RideStatusIncorrectException(messageSource.getMessage(
                RideExceptionMessageKeys.RIDE_STATUS_DECLINED_MESSAGE,
                new Object[] {},
                LocaleContextHolder.getLocale()
            ));
        }

        switch (currentStatus) {
            case CREATED -> checkStatus(currentStatus, newStatus, RideStatus.ACCEPTED);
            case ACCEPTED -> checkStatus(currentStatus, newStatus, RideStatus.ON_THE_WAY_TO_PASSENGER);
            case ON_THE_WAY_TO_PASSENGER -> checkStatus(currentStatus, newStatus, RideStatus.ON_THE_WAY_TO_DESTINATION);
            case ON_THE_WAY_TO_DESTINATION -> checkStatus(currentStatus, newStatus, RideStatus.FINISHED);
            default -> throw new RideStatusIncorrectException(messageSource.getMessage(
                RideExceptionMessageKeys.RIDE_STATUS_INCORRECT_MESSAGE,
                new Object[] {currentStatus, newStatus},
                LocaleContextHolder.getLocale()
            ));
        }
    }

    private void checkStatus(RideStatus currentStatus,
                             RideStatus newStatus,
                             RideStatus neededStatus) {
        if (newStatus != neededStatus && newStatus != RideStatus.DECLINED) {
            throw new RideStatusIncorrectException(messageSource.getMessage(
                RideExceptionMessageKeys.RIDE_STATUS_INCORRECT_MESSAGE,
                new Object[] {currentStatus, newStatus},
                LocaleContextHolder.getLocale()
            ));
        }
    }

}
