package by.modsen.ridesservice.controller;

import by.modsen.ridesservice.dto.request.RideRequest;
import by.modsen.ridesservice.dto.request.RideStatusRequest;
import by.modsen.ridesservice.dto.response.PageResponse;
import by.modsen.ridesservice.dto.response.RideResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "Ride controller",
    description = "This controller contains CRUD operations for rides controller in rides service")
public interface RideOperations {

  @Operation(description = "Creates a new ride")
  RideResponse createRide(@RequestBody @Valid RideRequest rideRequest);

  @Operation(description = "Updates an existing ride")
  RideResponse updateRide(@PathVariable Long rideId,
                          @RequestBody @Valid RideRequest rideRequest);

  @Operation(description = "Updates status of ride")
  RideResponse updateRideStatus(@PathVariable Long rideId,
                                @RequestBody @Valid RideStatusRequest rideStatusRequest);

  @Operation(description = "Retrieving a page of rides with limits")
  PageResponse<RideResponse> getAllRides(
      @RequestParam(defaultValue = "0") @Min(0) Integer offset,
      @RequestParam(defaultValue = "10") @Min(1) @Max(100) Integer limit);

  @Operation(description = "Retrieving a page of rides by driver with limits")
  PageResponse<RideResponse> getAllRidesByDriver(
      @RequestParam(defaultValue = "0") @Min(0) Integer offset,
      @RequestParam(defaultValue = "10") @Min(1) @Max(100) Integer limit,
      @PathVariable Long driverId);

  @Operation(description = "Retrieving a page of rides by passenger with limits")
  PageResponse<RideResponse> getAllRidesByPassenger(
      @RequestParam(defaultValue = "0") @Min(0) Integer offset,
      @RequestParam(defaultValue = "10") @Min(1) @Max(100) Integer limit,
      @PathVariable Long passengerId);

  @Operation(description = "Deletes an existing ride")
  void deleteRideById(@PathVariable Long rideId);

  @Operation(description = "Retrieves a ride by id")
  RideResponse getRideById(@PathVariable Long rideId);

}
