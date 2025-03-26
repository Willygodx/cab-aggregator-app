package by.modsen.ridesservice.service;

import by.modsen.ridesservice.dto.request.RideRequest;
import by.modsen.ridesservice.dto.request.RideStatusRequest;
import by.modsen.ridesservice.dto.response.PageResponse;
import by.modsen.ridesservice.dto.response.RideResponse;
import java.util.UUID;

public interface RideService {

    RideResponse createRide(RideRequest ridesRequest, String languageTag);

    RideResponse updateRide(RideRequest ridesRequest, Long id);

    RideResponse updateRideStatus(RideStatusRequest ridesStatusRequest, Long id);

    PageResponse<RideResponse> getAllRides(Integer offset, Integer limit);

    PageResponse<RideResponse> getAllRidesByDriver(Integer offset,
                                                   Integer limit,
                                                   UUID driverId,
                                                   String languageTag);

    PageResponse<RideResponse> getAllRidesByPassenger(Integer offset,
                                                      Integer limit,
                                                      UUID passengerId,
                                                      String languageTag);

    void deleteRideById(Long rideId);

    RideResponse getRideById(Long rideId);

}
