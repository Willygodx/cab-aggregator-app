package by.modsen.driverservice.service.impl;

import by.modsen.driverservice.dto.request.DriverRequest;
import by.modsen.driverservice.dto.response.DriverResponse;
import by.modsen.driverservice.dto.response.PageResponse;
import by.modsen.driverservice.mapper.DriverMapper;
import by.modsen.driverservice.mapper.PageResponseMapper;
import by.modsen.driverservice.model.Car;
import by.modsen.driverservice.model.Driver;
import by.modsen.driverservice.repository.CarRepository;
import by.modsen.driverservice.repository.DriverRepository;
import by.modsen.driverservice.service.DriverService;
import by.modsen.driverservice.service.component.validation.CarServiceValidation;
import by.modsen.driverservice.service.component.validation.DriverServiceValidation;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DriverServiceImpl implements DriverService {

    private final DriverRepository driverRepository;
    private final CarRepository carRepository;
    private final DriverServiceValidation driverServiceValidation;
    private final CarServiceValidation carServiceValidation;
    private final DriverMapper driverMapper;
    private final PageResponseMapper pageResponseMapper;

    @Override
    public PageResponse<DriverResponse> getAllDrivers(Integer offset, Integer limit) {
        Page<DriverResponse> driversPageDto = driverRepository
            .findAllByIsDeletedIsFalse(PageRequest.of(offset, limit))
            .map(driverMapper::toResponse);

        return pageResponseMapper.toDto(driversPageDto);
    }

    @Override
    @Transactional
    public DriverResponse createDriver(DriverRequest driverRequest) {
        driverServiceValidation.restoreOption(driverRequest);
        driverServiceValidation.checkAlreadyExists(driverRequest);

        Driver driver = driverMapper.toEntity(driverRequest);

        driverRepository.save(driver);

        return driverMapper.toResponse(driver);
    }

    @Override
    @Transactional
    public DriverResponse updateDriverById(DriverRequest driverRequest, Long driverId) {
        driverServiceValidation.restoreOption(driverRequest);
        driverServiceValidation.checkAlreadyExists(driverRequest);

        Driver existingDriver = driverServiceValidation.findDriverByIdWithCheck(driverId);

        driverMapper.updateDriverFromDto(driverRequest, existingDriver);

        Driver driver = driverRepository.save(existingDriver);

        return driverMapper.toResponse(driver);
    }

    @Override
    @Transactional
    public void deleteDriverById(Long driverId) {
        Driver driver = driverServiceValidation.findDriverByIdWithCheck(driverId);
        driver.setIsDeleted(true);

        driver.getCars().clear();

        driverRepository.save(driver);
    }

    @Override
    public DriverResponse getDriverById(Long driverId) {
        Driver driver = driverServiceValidation.findDriverByIdWithCheck(driverId);

        return driverMapper.toResponse(driver);
    }

    @Override
    @Transactional
    public void addCarToDriver(Long driverId, Long carId) {
        Driver driver = driverServiceValidation.findDriverByIdWithCheck(driverId);
        Car car = carServiceValidation.findCarByIdWithCheck(carId);

        driver.getCars().add(car);
        car.getDrivers().add(driver);

        driverRepository.save(driver);
        carRepository.save(car);
    }

}
