package by.modsen.driverservice.service;

import by.modsen.driverservice.dto.request.CarRequest;
import by.modsen.driverservice.dto.response.CarResponse;
import by.modsen.driverservice.dto.response.PageResponse;

public interface CarService {

    PageResponse<CarResponse> getAllCars(Integer offset, Integer limit);

    CarResponse createCar(CarRequest carRequest);

    CarResponse updateCarById(CarRequest carRequest, Long carId);

    void deleteCarById(Long carId);

    CarResponse getCarById(Long carId);

}
