package by.modsen.driverservice.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import by.modsen.driverservice.constants.TestDataConstants;
import by.modsen.driverservice.constants.CarExceptionMessageKeys;
import by.modsen.driverservice.constants.DriverExceptionMessageKeys;
import by.modsen.driverservice.dto.request.DriverRequest;
import by.modsen.driverservice.dto.response.DriverResponse;
import by.modsen.driverservice.dto.response.PageResponse;
import by.modsen.driverservice.exception.car.CarNotFoundException;
import by.modsen.driverservice.exception.driver.DriverAlreadyExistsException;
import by.modsen.driverservice.exception.driver.DriverNotFoundException;
import by.modsen.driverservice.mapper.DriverMapper;
import by.modsen.driverservice.mapper.PageResponseMapper;
import by.modsen.driverservice.model.Car;
import by.modsen.driverservice.model.Driver;
import by.modsen.driverservice.repository.CarRepository;
import by.modsen.driverservice.repository.DriverRepository;
import by.modsen.driverservice.service.component.validation.CarServiceValidation;
import by.modsen.driverservice.service.component.validation.DriverServiceValidation;
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
class DriverServiceImplTest {

    @Mock
    private DriverRepository driverRepository;

    @Mock
    private CarRepository carRepository;

    @Mock
    private DriverServiceValidation driverServiceValidation;

    @Mock
    private CarServiceValidation carServiceValidation;

    @Mock
    private DriverMapper driverMapper;

    @Mock
    private PageResponseMapper pageResponseMapper;

    @InjectMocks
    private DriverServiceImpl driverService;

    @Test
    void getAllDrivers_ReturnsPageDriverResponse_ValidInputArguments() {
        int offset = TestDataConstants.PAGE_OFFSET;
        int limit = TestDataConstants.PAGE_LIMIT;
        PageResponse<DriverResponse> expectedResponse = PageResponse.<DriverResponse>builder()
            .addCurrentOffset(offset)
            .addCurrentLimit(limit)
            .addTotalPages(1)
            .addTotalElements(1)
            .addSort("id")
            .addValues(Collections.singletonList(TestDataConstants.DRIVER_RESPONSE))
            .build();
        List<Driver> drivers = Collections.singletonList(TestDataConstants.DRIVER);
        Page<Driver> driverPage = new PageImpl<>(drivers);
        when(driverRepository.findAllByIsDeletedIsFalse(any(Pageable.class)))
            .thenReturn(driverPage);
        arrangeForTestingPageMethods(expectedResponse);

        PageResponse<DriverResponse> actual = driverService.getAllDrivers(offset, limit);

        assertThat(actual).isSameAs(expectedResponse);
        verify(driverMapper).toResponse(TestDataConstants.DRIVER);
        verify(pageResponseMapper).toDto(ArgumentMatchers.<Page<DriverResponse>>any());
    }

    @Test
    void createDriver_ReturnsDriverResponse_ValidInputArguments() {
        DriverRequest driverRequest = TestDataConstants.DRIVER_REQUEST;
        Driver driver = TestDataConstants.DRIVER;
        DriverResponse expectedResponse = TestDataConstants.DRIVER_RESPONSE;

        doNothing().when(driverServiceValidation).restoreOption(driverRequest);
        doNothing().when(driverServiceValidation).checkAlreadyExists(driverRequest);

        when(driverMapper.toEntity(driverRequest)).thenReturn(driver);
        when(driverRepository.save(driver)).thenReturn(driver);
        when(driverMapper.toResponse(driver)).thenReturn(expectedResponse);

        DriverResponse actual = driverService.createDriver(driverRequest);

        assertThat(actual).isEqualTo(expectedResponse);
        verify(driverServiceValidation).restoreOption(driverRequest);
        verify(driverServiceValidation).checkAlreadyExists(driverRequest);
        verify(driverMapper).toEntity(driverRequest);
        verify(driverRepository).save(driver);
        verify(driverMapper).toResponse(driver);
    }

    @Test
    void createDriver_ThrowsException_DriverWithEmailAlreadyExists() {
        DriverRequest driverRequest = TestDataConstants.DRIVER_REQUEST;
        String messageKey = DriverExceptionMessageKeys.DRIVER_ALREADY_EXISTS_BY_EMAIL_MESSAGE_KEY;
        Object[] args = new Object[]{driverRequest.email()};

        doThrow(new DriverAlreadyExistsException(messageKey, args))
            .when(driverServiceValidation).checkAlreadyExists(driverRequest);

        assertThatThrownBy(() -> driverService.createDriver(driverRequest))
            .isInstanceOf(DriverAlreadyExistsException.class)
            .extracting("messageKey", "args")
            .containsExactly(messageKey, args);

        verify(driverServiceValidation).checkAlreadyExists(driverRequest);
        verify(driverRepository, never()).save(any(Driver.class));
    }

    @Test
    void createDriver_ThrowsException_DriverWithPhoneAlreadyExists() {
        DriverRequest driverRequest = TestDataConstants.DRIVER_REQUEST;
        String messageKey = DriverExceptionMessageKeys.DRIVER_ALREADY_EXISTS_BY_PHONE_KEY;
        Object[] args = new Object[]{driverRequest.phoneNumber()};

        doThrow(new DriverAlreadyExistsException(messageKey, args))
            .when(driverServiceValidation).checkAlreadyExists(driverRequest);

        assertThatThrownBy(() -> driverService.createDriver(driverRequest))
            .isInstanceOf(DriverAlreadyExistsException.class)
            .extracting("messageKey", "args")
            .containsExactly(messageKey, args);

        verify(driverServiceValidation).checkAlreadyExists(driverRequest);
        verify(driverRepository, never()).save(any(Driver.class));
    }

    @Test
    void updateDriverById_ReturnsDriverResponse_ValidInputArguments() {
        DriverRequest driverRequest = TestDataConstants.DRIVER_REQUEST;
        Long driverId = TestDataConstants.DRIVER_ID;
        Driver existingDriver = TestDataConstants.DRIVER;
        Driver updatedDriver = TestDataConstants.DRIVER;
        DriverResponse expectedResponse = TestDataConstants.DRIVER_RESPONSE;

        doNothing().when(driverServiceValidation).restoreOption(driverRequest);
        doNothing().when(driverServiceValidation).checkAlreadyExists(driverRequest);
        when(driverServiceValidation.findDriverByIdWithCheck(driverId)).thenReturn(existingDriver);
        when(driverRepository.save(existingDriver)).thenReturn(updatedDriver);
        when(driverMapper.toResponse(updatedDriver)).thenReturn(expectedResponse);

        DriverResponse actual = driverService.updateDriverById(driverRequest, driverId);

        assertThat(actual).isEqualTo(expectedResponse);
        verify(driverServiceValidation).restoreOption(driverRequest);
        verify(driverServiceValidation).checkAlreadyExists(driverRequest);
        verify(driverServiceValidation).findDriverByIdWithCheck(driverId);
        verify(driverMapper).updateDriverFromDto(driverRequest, existingDriver);
        verify(driverRepository).save(existingDriver);
        verify(driverMapper).toResponse(updatedDriver);
    }

    @Test
    void updateDriverById_ThrowsException_DriverNotFound() {
        DriverRequest driverRequest = TestDataConstants.DRIVER_REQUEST;
        Long driverId = TestDataConstants.DRIVER_ID;
        String messageKey = DriverExceptionMessageKeys.DRIVER_NOT_FOUND_MESSAGE_KEY;
        Object[] args = new Object[]{driverId};

        when(driverServiceValidation.findDriverByIdWithCheck(driverId))
            .thenThrow(new DriverNotFoundException(messageKey, args));

        assertThatThrownBy(() -> driverService.updateDriverById(driverRequest, driverId))
            .isInstanceOf(DriverNotFoundException.class)
            .extracting("messageKey", "args")
            .containsExactly(messageKey, args);

        verify(driverServiceValidation).findDriverByIdWithCheck(driverId);
        verify(driverRepository, never()).save(any(Driver.class));
    }

    @Test
    void deleteDriverById_MarksDriverAsDeleted_ValidInputArguments() {
        Long driverId = TestDataConstants.DRIVER_ID;
        Driver driver = TestDataConstants.DRIVER;

        when(driverServiceValidation.findDriverByIdWithCheck(driverId))
            .thenReturn(driver);
        when(driverRepository.save(driver)).thenReturn(driver);

        driverService.deleteDriverById(driverId);

        assertThat(driver.getIsDeleted()).isTrue();
        verify(driverServiceValidation).findDriverByIdWithCheck(driverId);
        verify(driverRepository).save(driver);
    }

    @Test
    void deleteDriverById_ThrowsException_DriverNotFound() {
        Long driverId = TestDataConstants.DRIVER_ID;
        String messageKey = DriverExceptionMessageKeys.DRIVER_NOT_FOUND_MESSAGE_KEY;
        Object[] args = new Object[]{driverId};

        when(driverServiceValidation.findDriverByIdWithCheck(driverId))
            .thenThrow(new DriverNotFoundException(messageKey, args));

        assertThatThrownBy(() -> driverService.deleteDriverById(driverId))
            .isInstanceOf(DriverNotFoundException.class)
            .extracting("messageKey", "args")
            .containsExactly(messageKey, args);

        verify(driverServiceValidation).findDriverByIdWithCheck(driverId);
        verify(driverRepository, never()).save(any(Driver.class));
    }

    @Test
    void getDriverById_ReturnsDriverResponse_ValidInputArguments() {
        Long driverId = TestDataConstants.DRIVER_ID;
        Driver driver = TestDataConstants.DRIVER;
        DriverResponse expectedResponse = TestDataConstants.DRIVER_RESPONSE;

        when(driverServiceValidation.findDriverByIdWithCheck(driverId))
            .thenReturn(driver);
        when(driverMapper.toResponse(driver))
            .thenReturn(expectedResponse);

        DriverResponse actual = driverService.getDriverById(driverId);

        assertThat(actual).isEqualTo(expectedResponse);
        verify(driverServiceValidation).findDriverByIdWithCheck(driverId);
        verify(driverMapper).toResponse(driver);
    }

    @Test
    void getDriverById_ThrowsException_DriverNotFound() {
        Long driverId = TestDataConstants.DRIVER_ID;
        String messageKey = DriverExceptionMessageKeys.DRIVER_NOT_FOUND_MESSAGE_KEY;
        Object[] args = new Object[]{driverId};

        when(driverServiceValidation.findDriverByIdWithCheck(driverId))
            .thenThrow(new DriverNotFoundException(messageKey, args));

        assertThatThrownBy(() -> driverService.getDriverById(driverId))
            .isInstanceOf(DriverNotFoundException.class)
            .extracting("messageKey", "args")
            .containsExactly(messageKey, args);

        verify(driverServiceValidation).findDriverByIdWithCheck(driverId);
        verify(driverMapper, never()).toResponse(any(Driver.class));
    }

    @Test
    void addCarToDriver_AddsCarToDriver_ValidInputArguments() {
        Long driverId = TestDataConstants.DRIVER_ID;
        Long carId = TestDataConstants.CAR_ID;
        Driver driver = TestDataConstants.DRIVER;
        Car car = TestDataConstants.CAR;

        when(driverServiceValidation.findDriverByIdWithCheck(driverId))
            .thenReturn(driver);
        when(carServiceValidation.findCarByIdWithCheck(carId))
            .thenReturn(car);
        when(driverRepository.save(driver)).thenReturn(driver);
        when(carRepository.save(car)).thenReturn(car);

        driverService.addCarToDriver(driverId, carId);

        assertThat(driver.getCars()).contains(car);
        assertThat(car.getDrivers()).contains(driver);
        verify(driverServiceValidation).findDriverByIdWithCheck(driverId);
        verify(carServiceValidation).findCarByIdWithCheck(carId);
        verify(driverRepository).save(driver);
        verify(carRepository).save(car);
    }

    @Test
    void addCarToDriver_ThrowsException_DriverNotFound() {
        Long driverId = TestDataConstants.DRIVER_ID;
        Long carId = TestDataConstants.CAR_ID;
        String messageKey = DriverExceptionMessageKeys.DRIVER_NOT_FOUND_MESSAGE_KEY;
        Object[] args = new Object[]{driverId};

        when(driverServiceValidation.findDriverByIdWithCheck(driverId))
            .thenThrow(new DriverNotFoundException(messageKey, args));

        assertThatThrownBy(() -> driverService.addCarToDriver(driverId, carId))
            .isInstanceOf(DriverNotFoundException.class)
            .extracting("messageKey", "args")
            .containsExactly(messageKey, args);

        verify(driverServiceValidation).findDriverByIdWithCheck(driverId);
        verify(carServiceValidation, never()).findCarByIdWithCheck(any(Long.class));
        verify(driverRepository, never()).save(any(Driver.class));
        verify(carRepository, never()).save(any(Car.class));
    }

    @Test
    void addCarToDriver_ThrowsException_CarNotFound() {
        Long driverId = TestDataConstants.DRIVER_ID;
        Long carId = TestDataConstants.CAR_ID;
        String messageKey = CarExceptionMessageKeys.CAR_NOT_FOUND_MESSAGE_KEY;
        Object[] args = new Object[]{carId};

        when(driverServiceValidation.findDriverByIdWithCheck(driverId))
            .thenReturn(TestDataConstants.DRIVER);
        when(carServiceValidation.findCarByIdWithCheck(carId))
            .thenThrow(new CarNotFoundException(messageKey, args));

        assertThatThrownBy(() -> driverService.addCarToDriver(driverId, carId))
            .isInstanceOf(CarNotFoundException.class)
            .extracting("messageKey", "args")
            .containsExactly(messageKey, args);

        verify(driverServiceValidation).findDriverByIdWithCheck(driverId);
        verify(carServiceValidation).findCarByIdWithCheck(carId);
        verify(driverRepository, never()).save(any(Driver.class));
        verify(carRepository, never()).save(any(Car.class));
    }

    private void arrangeForTestingPageMethods(PageResponse<DriverResponse> expectedResponse) {
        when(driverMapper.toResponse(any(Driver.class)))
            .thenReturn(TestDataConstants.DRIVER_RESPONSE);
        when(pageResponseMapper.toDto(ArgumentMatchers.<Page<DriverResponse>>any()))
            .thenReturn(expectedResponse);
    }

}