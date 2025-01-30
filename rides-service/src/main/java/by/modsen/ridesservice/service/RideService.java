package by.modsen.ridesservice.service;

import by.modsen.ridesservice.dto.request.RideRequest;
import by.modsen.ridesservice.dto.request.RideStatusRequest;
import by.modsen.ridesservice.dto.response.PageResponse;
import by.modsen.ridesservice.dto.response.RideResponse;

public interface RideService {

  RideResponse createRide(RideRequest ridesRequest);

  RideResponse updateRide(RideRequest ridesRequest, Long id);

  RideResponse updateRideStatus(RideStatusRequest ridesStatusRequest, Long id);

  PageResponse<RideResponse> getAllRides(Integer offset, Integer limit);

  PageResponse<RideResponse> getAllRidesByDriver(Integer offset, Integer limit, Long driverId);

  PageResponse<RideResponse> getAllRidesByPassenger(Integer offset, Integer limit, Long passengerId);

  void deleteRideById(Long rideId);

  RideResponse getRideById(Long rideId);

}
