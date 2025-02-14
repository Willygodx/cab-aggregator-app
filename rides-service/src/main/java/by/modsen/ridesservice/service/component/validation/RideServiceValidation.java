package by.modsen.ridesservice.service.component.validation;

import by.modsen.ridesservice.dto.request.RideStatusRequest;
import by.modsen.ridesservice.model.Ride;

public interface RideServiceValidation {

    Ride findRideByIdWithCheck(Long rideId);

    void checkRideExistsById(Long rideId);

    void validChangeRideStatus(Ride ride, RideStatusRequest ridesStatusRequest);

    void checkPassengerExists(Long passengerId, String languageTag);

    void checkDriverExists(Long driverId, String languageTag);

}
