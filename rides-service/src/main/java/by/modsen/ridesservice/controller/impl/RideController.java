package by.modsen.ridesservice.controller.impl;

import by.modsen.ridesservice.aspect.ValidateAccessDriver;
import by.modsen.ridesservice.aspect.ValidateAccessPassenger;
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
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
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
    @PreAuthorize("hasRole('ADMIN') or hasRole('PASSENGER')")
    public RideResponse createRide(@RequestBody @Valid RideRequest rideRequest) {
        String languageTag = LocaleContextHolder.getLocale().toLanguageTag();

        return rideService.createRide(rideRequest, languageTag);
    }

    @PutMapping("/{rideId}")
    @Validated({Marker.OnUpdate.class})
    @PreAuthorize("hasRole('ADMIN') or hasRole('PASSENGER')")
    public RideResponse updateRide(@PathVariable Long rideId,
                                   @RequestBody @Valid RideRequest ridesRequest) {
        return rideService.updateRide(ridesRequest, rideId);
    }

    @PatchMapping("/status/{rideId}")
    @Validated({Marker.OnUpdate.class})
    @PreAuthorize("hasRole('ADMIN') or hasRole('PASSENGER') or hasRole('DRIVER')")
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
    @PreAuthorize("hasRole('ADMIN') or hasRole('DRIVER')")
    @ValidateAccessDriver
    public PageResponse<RideResponse> getAllRidesByDriver(
        @PathVariable UUID driverId,
        @RequestParam(defaultValue = "0") @Min(0) Integer offset,
        @RequestParam(defaultValue = "10") @Min(1) @Max(100) Integer limit,
        JwtAuthenticationToken jwt) {
        String languageTag = LocaleContextHolder.getLocale().toLanguageTag();

        return rideService.getAllRidesByDriver(offset, limit, driverId, languageTag);
    }

    @GetMapping("/passengers/{passengerId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('PASSENGER')")
    @ValidateAccessPassenger
    public PageResponse<RideResponse> getAllRidesByPassenger(
        @PathVariable UUID passengerId,
        @RequestParam(defaultValue = "0") @Min(0) Integer offset,
        @RequestParam(defaultValue = "10") @Min(1) @Max(100) Integer limit,
        JwtAuthenticationToken jwt) {
        String languageTag = LocaleContextHolder.getLocale().toLanguageTag();

        return rideService.getAllRidesByPassenger(offset, limit, passengerId, languageTag);
    }

    @DeleteMapping("/{rideId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteRideById(@PathVariable Long rideId) {
        rideService.deleteRideById(rideId);
    }

    @GetMapping("/{rideId}")
    public RideResponse getRideById(@PathVariable Long rideId) {
        return rideService.getRideById(rideId);
    }

}
