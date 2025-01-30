package by.modsen.driverservice.controller.impl;

import by.modsen.driverservice.controller.CarOperations;
import by.modsen.driverservice.dto.Marker;
import by.modsen.driverservice.dto.request.CarRequest;
import by.modsen.driverservice.dto.response.CarResponse;
import by.modsen.driverservice.dto.response.PageResponse;
import by.modsen.driverservice.service.CarService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
@RequestMapping("api/v1/cars")
@Validated
public class CarController implements CarOperations {

  private final CarService carService;

  @GetMapping
  public PageResponse<CarResponse> getAllCars(@RequestParam(defaultValue = "0") @Min(0) Integer offset,
                                              @RequestParam(defaultValue = "10") @Min(1) @Max(100) Integer limit) {
    return carService.getAllCars(offset, limit);
  }

  @GetMapping("/{carId}")
  public CarResponse getCarById(@PathVariable Long carId) {
    return carService.getCarById(carId);
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  @Validated(Marker.OnCreate.class)
  public CarResponse createCar(@RequestBody @Valid CarRequest carRequest) {
    return carService.createCar(carRequest);
  }

  @PutMapping("/{carId}")
  @Validated(Marker.OnUpdate.class)
  public CarResponse updateCarById(@PathVariable Long carId,
                                   @RequestBody @Valid CarRequest carRequest) {
    return carService.updateCarById(carRequest, carId);
  }

  @DeleteMapping("/{carId}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteCarById(@PathVariable Long carId) {
    carService.deleteCarById(carId);
  }

}
