package by.modsen.driverservice.service.component.validation.impl;

import by.modsen.driverservice.constants.CarExceptionMessageKeys;
import by.modsen.driverservice.dto.request.CarRequest;
import by.modsen.driverservice.exception.car.CarNotFoundException;
import by.modsen.driverservice.exception.car.CarNumberAlreadyExistsException;
import by.modsen.driverservice.model.Car;
import by.modsen.driverservice.repository.CarRepository;
import by.modsen.driverservice.service.component.validation.CarServiceValidation;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CarServiceValidationImpl implements CarServiceValidation {

    private final CarRepository carRepository;

    public void restoreOption(CarRequest carRequest) {
        String carNumber = carRequest.carNumber();

        if (Objects.nonNull(carNumber) && carRepository.existsCarByCarNumberAndIsDeletedIsTrue(carNumber)) {
            throw new CarNumberAlreadyExistsException(
                CarExceptionMessageKeys.CAR_RESTORE_NUMBER_OPTION_MESSAGE_KEY,
                carNumber
            );
        }
    }

    public void checkAlreadyExists(CarRequest carRequest) {
        String carNumber = carRequest.carNumber();

        if (carRepository.existsCarByCarNumberAndIsDeletedIsFalse(carNumber)) {
            throw new CarNumberAlreadyExistsException(
                CarExceptionMessageKeys.CAR_ALREADY_EXISTS_MESSAGE_KEY,
                carNumber
            );
        }
    }

    public Car findCarByIdWithCheck(Long id) {
        return carRepository.findCarByIdAndIsDeletedIsFalse(id)
            .orElseThrow(() -> new CarNotFoundException(
                CarExceptionMessageKeys.CAR_NOT_FOUND_MESSAGE_KEY,
                id
            ));
    }

}
