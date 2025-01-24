package by.modsen.driverservice.service.impl;

import by.modsen.driverservice.dto.request.CarRequest;
import by.modsen.driverservice.dto.response.CarResponse;
import by.modsen.driverservice.dto.response.PageResponse;
import by.modsen.driverservice.mapper.CarMapper;
import by.modsen.driverservice.mapper.PageResponseMapper;
import by.modsen.driverservice.model.Car;
import by.modsen.driverservice.model.Driver;
import by.modsen.driverservice.repository.CarRepository;
import by.modsen.driverservice.repository.DriverRepository;
import by.modsen.driverservice.service.CarService;
import by.modsen.driverservice.service.component.validation.CarServiceValidation;
import by.modsen.driverservice.service.component.validation.DriverServiceValidation;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CarServiceImpl implements CarService {

  private final CarRepository carRepository;
  private final DriverRepository driverRepository;
  private final CarServiceValidation carServiceValidation;
  private final CarMapper carMapper;
  private final DriverServiceValidation driverServiceValidation;
  private final PageResponseMapper pageResponseMapper;

  @Override
  public PageResponse<CarResponse> getAllCars(Integer offset, Integer limit) {
    Page<CarResponse> carsPageDto = carRepository
        .findAllByIsDeletedIsFalse(PageRequest.of(offset, limit))
        .map(carMapper::toResponse);

    return pageResponseMapper.toDto(carsPageDto);
  }

  @Override
  @Transactional
  public CarResponse createCar(CarRequest carRequest, Long driverId) {
    carServiceValidation.restoreOption(carRequest);
    carServiceValidation.checkAlreadyExists(carRequest);

    Car car = carMapper.toEntity(carRequest);
    car.setIsDeleted(false);

    Driver driver = driverServiceValidation.findDriverByIdWithCheck(driverId);
    driver.setIsDeleted(false);

    car.setDriver(driver);
    driver.getCars().add(car);

    carRepository.save(car);
    driverRepository.save(driver);

    return carMapper.toResponse(car);
  }

  @Override
  @Transactional
  public CarResponse updateCarById(CarRequest carRequest, Long carId) {
    carServiceValidation.restoreOption(carRequest);
    carServiceValidation.checkAlreadyExists(carRequest);

    Car car = carServiceValidation.findCarByIdWithCheck(carId);
    car.setIsDeleted(false);

    carMapper.updateCarFromDto(carRequest, car);

    carRepository.save(car);

    return carMapper.toResponse(car);
  }

  @Override
  @Transactional
  public void deleteCarById(Long carId) {
    Car car = carServiceValidation.findCarByIdWithCheck(carId);
    car.setIsDeleted(true);

    carRepository.save(car);
  }

  @Override
  public CarResponse getCarById(Long carId) {
    Car car = carServiceValidation.findCarByIdWithCheck(carId);

    return carMapper.toResponse(car);
  }

}
