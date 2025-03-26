package by.modsen.ridesservice.service.component.validation;

import by.modsen.ridesservice.dto.request.RideStatusRequest;
import by.modsen.ridesservice.model.Ride;
import java.util.UUID;

public interface RideServiceValidation {

    Ride findRideByIdWithCheck(Long rideId);

    void checkRideExistsById(Long rideId);

    void validChangeRideStatus(Ride ride, RideStatusRequest ridesStatusRequest);

    void checkPassengerExists(UUID passengerId, String languageTag, String authorization);

    void checkDriverExists(UUID driverId, String languageTag, String authorization);

}
