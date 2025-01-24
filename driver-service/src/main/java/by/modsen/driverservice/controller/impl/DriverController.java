package by.modsen.driverservice.controller.impl;

import by.modsen.driverservice.controller.DriverOperations;
import by.modsen.driverservice.dto.request.DriverRequest;
import by.modsen.driverservice.dto.response.DriverResponse;
import by.modsen.driverservice.dto.response.PageResponse;
import by.modsen.driverservice.service.DriverService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
public class DriverController implements DriverOperations {

  private final DriverService driverService;

  @GetMapping
  public PageResponse<DriverResponse> getAllDrivers(@RequestParam(defaultValue = "0") @Min(0) Integer offset,
                                                    @RequestParam(defaultValue = "10") @Min(1) @Max(100) Integer limit) {
    return driverService.getAllDrivers(offset, limit);
  }

  @GetMapping("/{driverId}")
  public DriverResponse getDriverById(@PathVariable Long driverId) {
    return driverService.getDriverById(driverId);
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public DriverResponse createDriver(@RequestBody @Valid DriverRequest driverDto) {
    return driverService.createDriver(driverDto);
  }

  @PutMapping("/{driverId}")
  public DriverResponse updateDriverById(@PathVariable Long driverId,
                                         @RequestBody @Valid DriverRequest driverDto) {
    return driverService.updateDriverById(driverDto, driverId);
  }

  @DeleteMapping("/{driverId}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteDriverById(@PathVariable Long driverId) {
    driverService.deleteDriverById(driverId);
  }

}
