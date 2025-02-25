package by.modsen.driverservice.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import by.modsen.driverservice.constants.TestDataConstants;
import by.modsen.driverservice.constants.CarExceptionMessageKeys;
import by.modsen.driverservice.dto.request.CarRequest;
import by.modsen.driverservice.dto.response.CarResponse;
import by.modsen.driverservice.dto.response.PageResponse;
import by.modsen.driverservice.exception.car.CarNotFoundException;
import by.modsen.driverservice.exception.car.CarNumberAlreadyExistsException;
import by.modsen.driverservice.mapper.CarMapper;
import by.modsen.driverservice.mapper.PageResponseMapper;
import by.modsen.driverservice.model.Car;
import by.modsen.driverservice.repository.CarRepository;
import by.modsen.driverservice.service.component.validation.CarServiceValidation;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class CarServiceImplTest {

    @Mock
    private CarRepository carRepository;

    @Mock
    private CarServiceValidation carServiceValidation;

    @Mock
    private CarMapper carMapper;

    @Mock
    private PageResponseMapper pageResponseMapper;

    @InjectMocks
    private CarServiceImpl carService;

    @Test
    void getAllCars_ReturnsPageCarResponse_ValidInputArguments() {
        int offset = TestDataConstants.PAGE_OFFSET;
        int limit = TestDataConstants.PAGE_LIMIT;
        PageResponse<CarResponse> expectedResponse = PageResponse.<CarResponse>builder()
            .addCurrentOffset(offset)
            .addCurrentLimit(limit)
            .addTotalPages(1)
            .addTotalElements(1)
            .addSort("id")
            .addValues(Collections.singletonList(TestDataConstants.CAR_RESPONSE))
            .build();
        List<Car> cars = Collections.singletonList(TestDataConstants.CAR);
        Page<Car> carPage = new PageImpl<>(cars);
        when(carRepository.findAllByIsDeletedIsFalse(any(Pageable.class)))
            .thenReturn(carPage);
        arrangeForTestingPageMethods(expectedResponse);

        PageResponse<CarResponse> actual = carService.getAllCars(offset, limit);

        assertThat(actual).isSameAs(expectedResponse);
        verify(carMapper).toResponse(TestDataConstants.CAR);
        verify(pageResponseMapper).toDto(ArgumentMatchers.<Page<CarResponse>>any());
    }

    @Test
    void createCar_ReturnsCarResponse_ValidInputArguments() {
        CarRequest carRequest = TestDataConstants.CAR_REQUEST;
        Car car = TestDataConstants.CAR;
        CarResponse expectedResponse = TestDataConstants.CAR_RESPONSE;

        doNothing().when(carServiceValidation).restoreOption(carRequest);
        doNothing().when(carServiceValidation).checkAlreadyExists(carRequest);

        when(carMapper.toEntity(carRequest)).thenReturn(car);
        when(carRepository.save(car)).thenReturn(car);
        when(carMapper.toResponse(car)).thenReturn(expectedResponse);

        CarResponse actual = carService.createCar(carRequest);

        assertThat(actual).isEqualTo(expectedResponse);
        verify(carServiceValidation).restoreOption(carRequest);
        verify(carServiceValidation).checkAlreadyExists(carRequest);
        verify(carMapper).toEntity(carRequest);
        verify(carRepository).save(car);
        verify(carMapper).toResponse(car);
    }

    @Test
    void createCar_ThrowsException_CarWithNumberAlreadyExists() {
        CarRequest carRequest = TestDataConstants.CAR_REQUEST;
        String messageKey = CarExceptionMessageKeys.CAR_ALREADY_EXISTS_MESSAGE_KEY;
        Object[] args = new Object[]{carRequest.carNumber()};

        doThrow(new CarNumberAlreadyExistsException(messageKey, args))
            .when(carServiceValidation).checkAlreadyExists(carRequest);

        assertThatThrownBy(() -> carService.createCar(carRequest))
            .isInstanceOf(CarNumberAlreadyExistsException.class)
            .extracting("messageKey", "args")
            .containsExactly(messageKey, args);

        verify(carServiceValidation).checkAlreadyExists(carRequest);
        verify(carRepository, never()).save(any(Car.class));
    }

    @Test
    void updateCarById_ReturnsCarResponse_ValidInputArguments() {
        CarRequest carRequest = TestDataConstants.CAR_REQUEST;
        Long carId = TestDataConstants.CAR_ID;
        Car existingCar = TestDataConstants.CAR;
        Car updatedCar = TestDataConstants.CAR;
        CarResponse expectedResponse = TestDataConstants.CAR_RESPONSE;

        doNothing().when(carServiceValidation).restoreOption(carRequest);
        doNothing().when(carServiceValidation).checkAlreadyExists(carRequest);
        when(carServiceValidation.findCarByIdWithCheck(carId)).thenReturn(existingCar);
        when(carRepository.save(existingCar)).thenReturn(updatedCar);
        when(carMapper.toResponse(updatedCar)).thenReturn(expectedResponse);

        CarResponse actual = carService.updateCarById(carRequest, carId);

        assertThat(actual).isEqualTo(expectedResponse);
        verify(carServiceValidation).restoreOption(carRequest);
        verify(carServiceValidation).checkAlreadyExists(carRequest);
        verify(carServiceValidation).findCarByIdWithCheck(carId);
        verify(carMapper).updateCarFromDto(carRequest, existingCar);
        verify(carRepository).save(existingCar);
        verify(carMapper).toResponse(updatedCar);
    }

    @Test
    void updateCarById_ThrowsException_CarNotFound() {
        CarRequest carRequest = TestDataConstants.CAR_REQUEST;
        Long carId = TestDataConstants.CAR_ID;
        String messageKey = CarExceptionMessageKeys.CAR_NOT_FOUND_MESSAGE_KEY;
        Object[] args = new Object[]{carId};

        when(carServiceValidation.findCarByIdWithCheck(carId))
            .thenThrow(new CarNotFoundException(messageKey, args));

        assertThatThrownBy(() -> carService.updateCarById(carRequest, carId))
            .isInstanceOf(CarNotFoundException.class)
            .extracting("messageKey", "args")
            .containsExactly(messageKey, args);

        verify(carServiceValidation).findCarByIdWithCheck(carId);
        verify(carRepository, never()).save(any(Car.class));
    }

    @Test
    void updateCarById_ThrowsException_CarWithNumberAlreadyExists() {
        CarRequest carRequest = TestDataConstants.CAR_REQUEST;
        Long carId = TestDataConstants.CAR_ID;
        Car existingCar = TestDataConstants.CAR;
        String messageKey = CarExceptionMessageKeys.CAR_ALREADY_EXISTS_MESSAGE_KEY;
        Object[] args = new Object[]{carRequest.carNumber()};

        doThrow(new CarNumberAlreadyExistsException(messageKey, args))
            .when(carServiceValidation).checkAlreadyExists(carRequest);

        lenient().when(carServiceValidation.findCarByIdWithCheck(carId))
            .thenReturn(existingCar);

        assertThatThrownBy(() -> carService.updateCarById(carRequest, carId))
            .isInstanceOf(CarNumberAlreadyExistsException.class)
            .extracting("messageKey", "args")
            .containsExactly(messageKey, args);

        verify(carServiceValidation).checkAlreadyExists(carRequest);
        verify(carRepository, never()).save(any(Car.class));
    }

    @Test
    void deleteCarById_MarksCarAsDeleted_ValidInputArguments() {
        Long carId = TestDataConstants.CAR_ID;
        Car car = TestDataConstants.CAR;

        when(carServiceValidation.findCarByIdWithCheck(carId))
            .thenReturn(car);
        when(carRepository.save(car)).thenReturn(car);

        carService.deleteCarById(carId);

        assertThat(car.getIsDeleted()).isTrue();
        verify(carServiceValidation).findCarByIdWithCheck(carId);
        verify(carRepository).save(car);
    }

    @Test
    void deleteCarById_ThrowsException_CarNotFound() {
        Long carId = TestDataConstants.CAR_ID;
        String messageKey = CarExceptionMessageKeys.CAR_NOT_FOUND_MESSAGE_KEY;
        Object[] args = new Object[]{carId};

        when(carServiceValidation.findCarByIdWithCheck(carId))
            .thenThrow(new CarNotFoundException(messageKey, args));

        assertThatThrownBy(() -> carService.deleteCarById(carId))
            .isInstanceOf(CarNotFoundException.class)
            .extracting("messageKey", "args")
            .containsExactly(messageKey, args);

        verify(carServiceValidation).findCarByIdWithCheck(carId);
        verify(carRepository, never()).save(any(Car.class));
    }

    @Test
    void getCarById_ReturnsCarResponse_ValidInputArguments() {
        Long carId = TestDataConstants.CAR_ID;
        Car car = TestDataConstants.CAR;
        CarResponse expectedResponse = TestDataConstants.CAR_RESPONSE;

        when(carServiceValidation.findCarByIdWithCheck(carId))
            .thenReturn(car);
        when(carMapper.toResponse(car))
            .thenReturn(expectedResponse);

        CarResponse actual = carService.getCarById(carId);

        assertThat(actual).isEqualTo(expectedResponse);
        verify(carServiceValidation).findCarByIdWithCheck(carId);
        verify(carMapper).toResponse(car);
    }

    @Test
    void getCarById_ThrowsException_CarNotFound() {
        Long carId = TestDataConstants.CAR_ID;
        String messageKey = CarExceptionMessageKeys.CAR_NOT_FOUND_MESSAGE_KEY;
        Object[] args = new Object[]{carId};

        when(carServiceValidation.findCarByIdWithCheck(carId))
            .thenThrow(new CarNotFoundException(messageKey, args));

        assertThatThrownBy(() -> carService.getCarById(carId))
            .isInstanceOf(CarNotFoundException.class)
            .extracting("messageKey", "args")
            .containsExactly(messageKey, args);

        verify(carServiceValidation).findCarByIdWithCheck(carId);
        verify(carMapper, never()).toResponse(any(Car.class));
    }

    private void arrangeForTestingPageMethods(PageResponse<CarResponse> expectedResponse) {
        when(carMapper.toResponse(any(Car.class)))
            .thenReturn(TestDataConstants.CAR_RESPONSE);
        when(pageResponseMapper.toDto(ArgumentMatchers.<Page<CarResponse>>any()))
            .thenReturn(expectedResponse);
    }

}