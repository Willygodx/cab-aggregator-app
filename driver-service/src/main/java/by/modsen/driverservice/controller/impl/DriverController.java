package by.modsen.driverservice.controller.impl;

import by.modsen.driverservice.aspect.ValidateAccess;
import by.modsen.driverservice.controller.DriverOperations;
import by.modsen.driverservice.dto.Marker;
import by.modsen.driverservice.dto.request.DriverRequest;
import by.modsen.driverservice.dto.response.DriverResponse;
import by.modsen.driverservice.dto.response.PageResponse;
import by.modsen.driverservice.service.DriverService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
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
@RequestMapping("api/v1/drivers")
@Validated
public class DriverController implements DriverOperations {

    private final DriverService driverService;

    @GetMapping
    public PageResponse<DriverResponse> getAllDrivers(@RequestParam(defaultValue = "0") @Min(0) Integer offset,
                                                      @RequestParam(defaultValue = "10")
                                                      @Min(1) @Max(100) Integer limit) {
        return driverService.getAllDrivers(offset, limit);
    }

    @GetMapping("/{driverId}")
    public DriverResponse getDriverById(@PathVariable UUID driverId) {
        return driverService.getDriverById(driverId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Validated(Marker.OnCreate.class)
    @PreAuthorize("hasRole('ADMIN')")
    public DriverResponse createDriver(@RequestBody @Valid DriverRequest driverRequest) {
        return driverService.createDriver(driverRequest);
    }

    @PutMapping("/{driverId}")
    @Validated(Marker.OnUpdate.class)
    @PreAuthorize("hasRole('ADMIN') or hasRole('DRIVER')")
    @ValidateAccess
    public DriverResponse updateDriverById(@PathVariable UUID driverId,
                                           @RequestBody @Valid DriverRequest driverRequest,
                                           JwtAuthenticationToken jwt) {
        return driverService.updateDriverById(driverRequest, driverId);
    }

    @DeleteMapping("/{driverId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN') or hasRole('DRIVER')")
    @ValidateAccess
    public void deleteDriverById(@PathVariable UUID driverId,
                                 JwtAuthenticationToken jwt) {
        driverService.deleteDriverById(driverId);
    }

    @PostMapping("/{driverId}/add-car/{carId}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ADMIN') or hasRole('DRIVER')")
    @ValidateAccess
    public void addCarToDriver(@PathVariable UUID driverId,
                               @PathVariable Long carId,
                               JwtAuthenticationToken jwt) {
        driverService.addCarToDriver(driverId, carId);
    }

}
