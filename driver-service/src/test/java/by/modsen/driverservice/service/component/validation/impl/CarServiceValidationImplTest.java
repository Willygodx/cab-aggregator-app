package by.modsen.driverservice.service.component.validation.impl;

import by.modsen.driverservice.constants.TestDataConstants;
import by.modsen.driverservice.constants.CarExceptionMessageKeys;
import by.modsen.driverservice.dto.request.CarRequest;
import by.modsen.driverservice.exception.car.CarNotFoundException;
import by.modsen.driverservice.exception.car.CarNumberAlreadyExistsException;
import by.modsen.driverservice.model.Car;
import by.modsen.driverservice.repository.CarRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CarServiceValidationImplTest {

    @Mock
    private CarRepository carRepository;

    @InjectMocks
    private CarServiceValidationImpl carServiceValidation;

    @Test
    void restoreOption_ThrowsException_CarWithNumberWasDeleted() {
        CarRequest carRequest = TestDataConstants.CAR_REQUEST;
        String carNumber = TestDataConstants.CAR_NUMBER;
        String messageKey = CarExceptionMessageKeys.CAR_RESTORE_NUMBER_OPTION_MESSAGE_KEY;
        Object[] args = new Object[]{carNumber};

        when(carRepository.existsCarByCarNumberAndIsDeletedIsTrue(carNumber))
            .thenReturn(true);

        assertThatThrownBy(() -> carServiceValidation.restoreOption(carRequest))
            .isInstanceOf(CarNumberAlreadyExistsException.class)
            .extracting("messageKey", "args")
            .containsExactly(messageKey, args);

        verify(carRepository).existsCarByCarNumberAndIsDeletedIsTrue(carNumber);
    }

    @Test
    void restoreOption_NoException_NoCarToRestore() {
        CarRequest carRequest = TestDataConstants.CAR_REQUEST;

        when(carRepository.existsCarByCarNumberAndIsDeletedIsTrue(TestDataConstants.CAR_NUMBER))
            .thenReturn(false);

        assertThatCode(() -> carServiceValidation.restoreOption(carRequest))
            .doesNotThrowAnyException();

        verify(carRepository).existsCarByCarNumberAndIsDeletedIsTrue(TestDataConstants.CAR_NUMBER);
    }

    @Test
    void checkAlreadyExists_ThrowsException_CarWithNumberAlreadyExists() {
        CarRequest carRequest = TestDataConstants.CAR_REQUEST;
        String carNumber = TestDataConstants.CAR_NUMBER;
        String messageKey = CarExceptionMessageKeys.CAR_ALREADY_EXISTS_MESSAGE_KEY;
        Object[] args = new Object[]{carNumber};

        when(carRepository.existsCarByCarNumberAndIsDeletedIsFalse(carNumber))
            .thenReturn(true);

        assertThatThrownBy(() -> carServiceValidation.checkAlreadyExists(carRequest))
            .isInstanceOf(CarNumberAlreadyExistsException.class)
            .extracting("messageKey", "args")
            .containsExactly(messageKey, args);

        verify(carRepository).existsCarByCarNumberAndIsDeletedIsFalse(carNumber);
    }

    @Test
    void checkAlreadyExists_NoException_NoCarExists() {
        CarRequest carRequest = TestDataConstants.CAR_REQUEST;

        when(carRepository.existsCarByCarNumberAndIsDeletedIsFalse(TestDataConstants.CAR_NUMBER))
            .thenReturn(false);

        assertThatCode(() -> carServiceValidation.checkAlreadyExists(carRequest))
            .doesNotThrowAnyException();

        verify(carRepository).existsCarByCarNumberAndIsDeletedIsFalse(TestDataConstants.CAR_NUMBER);
    }

    @Test
    void findCarByIdWithCheck_ReturnsCar_ValidInputArguments() {
        Long carId = TestDataConstants.CAR_ID;
        Car car = TestDataConstants.CAR;

        when(carRepository.findCarByIdAndIsDeletedIsFalse(carId))
            .thenReturn(Optional.of(car));

        Car result = carServiceValidation.findCarByIdWithCheck(carId);

        assertThat(result).isEqualTo(car);
        verify(carRepository).findCarByIdAndIsDeletedIsFalse(carId);
    }

    @Test
    void findCarByIdWithCheck_ThrowsException_CarNotFound() {
        Long carId = TestDataConstants.CAR_ID;
        String messageKey = CarExceptionMessageKeys.CAR_NOT_FOUND_MESSAGE_KEY;
        Object[] args = new Object[]{carId};

        when(carRepository.findCarByIdAndIsDeletedIsFalse(carId))
            .thenReturn(Optional.empty());

        assertThatThrownBy(() -> carServiceValidation.findCarByIdWithCheck(carId))
            .isInstanceOf(CarNotFoundException.class)
            .extracting("messageKey", "args")
            .containsExactly(messageKey, args);

        verify(carRepository).findCarByIdAndIsDeletedIsFalse(carId);
    }

}