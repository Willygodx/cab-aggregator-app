package by.modsen.ridesservice.controller.impl;

import by.modsen.ridesservice.controller.RideOperations;
import by.modsen.ridesservice.dto.Marker;
import by.modsen.ridesservice.dto.request.RideRequest;
import by.modsen.ridesservice.dto.request.RideStatusRequest;
import by.modsen.ridesservice.dto.response.PageResponse;
import by.modsen.ridesservice.dto.response.RideResponse;
import by.modsen.ridesservice.service.RideService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/rides")
@Validated
public class RideController implements RideOperations {

  private final RideService rideService;

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  @Validated({Marker.OnCreate.class})
  public RideResponse createRide(@RequestBody @Valid RideRequest rideRequest) {
    return rideService.createRide(rideRequest);
  }

  @PutMapping("/{rideId}")
  @Validated({Marker.OnUpdate.class})
  public RideResponse updateRide(@PathVariable Long rideId,
                                 @RequestBody @Valid RideRequest ridesRequest) {
    return rideService.updateRide(ridesRequest, rideId);
  }

  @PatchMapping("/status/{rideId}")
  @Validated({Marker.OnUpdate.class})
  public RideResponse updateRideStatus(@PathVariable Long rideId,
                                       @RequestBody
                                       @Valid RideStatusRequest ridesStatusRequest) {
    return rideService.updateRideStatus(ridesStatusRequest, rideId);
  }

  @GetMapping
  public PageResponse<RideResponse> getAllRides(
      @RequestParam(defaultValue = "0") @Min(0) Integer offset,
      @RequestParam(defaultValue = "10") @Min(1) @Max(100) Integer limit) {
    return rideService.getAllRides(offset, limit);
  }

  @GetMapping("/drivers/{driverId}")
  public PageResponse<RideResponse> getAllRidesByDriver(
      @RequestParam(defaultValue = "0") @Min(0) Integer offset,
      @RequestParam(defaultValue = "10") @Min(1) @Max(100) Integer limit,
      @PathVariable Long driverId) {
    return rideService.getAllRidesByDriver(offset, limit, driverId);
  }

  @GetMapping("/passengers/{passengerId}")
  public PageResponse<RideResponse> getAllRidesByPassenger(
      @RequestParam(defaultValue = "0") @Min(0) Integer offset,
      @RequestParam(defaultValue = "10") @Min(1) @Max(100) Integer limit,
      @PathVariable Long passengerId) {
    return rideService.getAllRidesByPassenger(offset, limit, passengerId);
  }

  @DeleteMapping("/{rideId}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteRideById(@PathVariable Long rideId) {
    rideService.deleteRideById(rideId);
  }

  @GetMapping("/{rideId}")
  public RideResponse getRideById(@PathVariable Long rideId) {
    return rideService.getRideById(rideId);
  }

}
