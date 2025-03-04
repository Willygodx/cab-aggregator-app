package by.modsen.ridesservice.service.impl;

import by.modsen.ridesservice.client.exception.FeignClientException;
import by.modsen.ridesservice.constants.RideExceptionMessageKeys;
import by.modsen.ridesservice.constants.TestDataConstants;
import by.modsen.ridesservice.dto.ExceptionDto;
import by.modsen.ridesservice.dto.request.RideRequest;
import by.modsen.ridesservice.dto.request.RideStatusRequest;
import by.modsen.ridesservice.dto.response.PageResponse;
import by.modsen.ridesservice.dto.response.RideResponse;
import by.modsen.ridesservice.exception.ride.RideNotFoundException;
import by.modsen.ridesservice.exception.ride.RideStatusIncorrectException;
import by.modsen.ridesservice.mapper.PageResponseMapper;
import by.modsen.ridesservice.mapper.RideMapper;
import by.modsen.ridesservice.model.Ride;
import by.modsen.ridesservice.repository.RideRepository;
import by.modsen.ridesservice.service.component.RideServicePriceGenerator;
import by.modsen.ridesservice.service.component.validation.RideServiceValidation;
import jakarta.validation.ConstraintViolationException;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RideServiceImplTest {

    @Mock
    private RideRepository rideRepository;

    @Mock
    private RideMapper rideMapper;

    @Mock
    private PageResponseMapper pageResponseMapper;

    @Mock
    private RideServiceValidation rideServiceValidation;

    @Mock
    private RideServicePriceGenerator rideServicePriceGenerator;

    @InjectMocks
    private RideServiceImpl rideService;

    @Test
    void createRide_ReturnsRideResponse_ValidInputArguments() {
        RideRequest rideRequest = TestDataConstants.RIDE_REQUEST;
        Ride ride = TestDataConstants.RIDE;
        RideResponse expectedResponse = TestDataConstants.RIDE_RESPONSE;

        doNothing().when(rideServiceValidation).checkDriverExists(rideRequest.driverId(), "en");
        doNothing().when(rideServiceValidation).checkPassengerExists(rideRequest.passengerId(), "en");
        when(rideServicePriceGenerator.generateRandomCost()).thenReturn(new BigDecimal("10.00"));
        when(rideMapper.toEntity(rideRequest, new BigDecimal("10.00"))).thenReturn(ride);
        when(rideRepository.save(ride)).thenReturn(ride);
        when(rideMapper.toResponse(ride)).thenReturn(expectedResponse);

        RideResponse actual = rideService.createRide(rideRequest, "en");

        assertThat(actual).isEqualTo(expectedResponse);
        verify(rideServiceValidation).checkDriverExists(rideRequest.driverId(), "en");
        verify(rideServiceValidation).checkPassengerExists(rideRequest.passengerId(), "en");
        verify(rideServicePriceGenerator).generateRandomCost();
        verify(rideMapper).toEntity(rideRequest, new BigDecimal("10.00"));
        verify(rideRepository).save(ride);
        verify(rideMapper).toResponse(ride);
    }

    @Test
    void updateRide_ReturnsRideResponse_ValidInputArguments() {
        RideRequest rideRequest = TestDataConstants.RIDE_REQUEST;
        Long rideId = TestDataConstants.RIDE_ID;
        Ride ride = TestDataConstants.RIDE;
        RideResponse expectedResponse = TestDataConstants.RIDE_RESPONSE;

        when(rideServiceValidation.findRideByIdWithCheck(rideId)).thenReturn(ride);
        when(rideRepository.save(ride)).thenReturn(ride);
        when(rideMapper.toResponse(ride)).thenReturn(expectedResponse);

        RideResponse actual = rideService.updateRide(rideRequest, rideId);

        assertThat(actual).isEqualTo(expectedResponse);
        verify(rideServiceValidation).findRideByIdWithCheck(rideId);
        verify(rideMapper).updateRideFromDto(rideRequest, ride);
        verify(rideRepository).save(ride);
        verify(rideMapper).toResponse(ride);
    }

    @Test
    void updateRide_ThrowsException_RideNotFound() {
        RideRequest rideRequest = TestDataConstants.RIDE_REQUEST;
        Long rideId = TestDataConstants.RIDE_ID;
        String messageKey = RideExceptionMessageKeys.RIDE_NOT_FOUND_MESSAGE;
        Object[] args = new Object[]{rideId};

        when(rideServiceValidation.findRideByIdWithCheck(rideId))
            .thenThrow(new RideNotFoundException(messageKey, args));

        assertThatThrownBy(() -> rideService.updateRide(rideRequest, rideId))
            .isInstanceOf(RideNotFoundException.class)
            .extracting("messageKey", "args")
            .containsExactly(messageKey, args);

        verify(rideServiceValidation).findRideByIdWithCheck(rideId);
        verify(rideRepository, never()).save(any(Ride.class));
    }

    @Test
    void updateRideStatus_ReturnsRideResponse_ValidInputArguments() {
        RideStatusRequest rideStatusRequest = TestDataConstants.RIDE_STATUS_REQUEST;
        Long rideId = TestDataConstants.RIDE_ID;
        Ride ride = TestDataConstants.RIDE;
        RideResponse expectedResponse = TestDataConstants.RIDE_RESPONSE;

        when(rideServiceValidation.findRideByIdWithCheck(rideId)).thenReturn(ride);
        doNothing().when(rideServiceValidation).validChangeRideStatus(ride, rideStatusRequest);
        when(rideRepository.save(ride)).thenReturn(ride);
        when(rideMapper.toResponse(ride)).thenReturn(expectedResponse);

        RideResponse actual = rideService.updateRideStatus(rideStatusRequest, rideId);

        assertThat(actual).isEqualTo(expectedResponse);
        verify(rideServiceValidation).findRideByIdWithCheck(rideId);
        verify(rideServiceValidation).validChangeRideStatus(ride, rideStatusRequest);
        verify(rideMapper).updateRideFromDto(rideStatusRequest, ride);
        verify(rideRepository).save(ride);
        verify(rideMapper).toResponse(ride);
    }

    @Test
    void updateRideStatus_ThrowsException_RideNotFound() {
        RideStatusRequest rideStatusRequest = TestDataConstants.RIDE_STATUS_REQUEST;
        Long rideId = TestDataConstants.RIDE_ID;
        String messageKey = RideExceptionMessageKeys.RIDE_NOT_FOUND_MESSAGE;
        Object[] args = new Object[]{rideId};

        when(rideServiceValidation.findRideByIdWithCheck(rideId))
            .thenThrow(new RideNotFoundException(messageKey, args));

        assertThatThrownBy(() -> rideService.updateRideStatus(rideStatusRequest, rideId))
            .isInstanceOf(RideNotFoundException.class)
            .extracting("messageKey", "args")
            .containsExactly(messageKey, args);

        verify(rideServiceValidation).findRideByIdWithCheck(rideId);
        verify(rideRepository, never()).save(any(Ride.class));
    }

    @Test
    void updateRideStatus_ThrowsException_RideStatusIncorrect() {
        RideStatusRequest rideStatusRequest = TestDataConstants.RIDE_STATUS_REQUEST;
        Long rideId = TestDataConstants.RIDE_ID;
        Ride ride = TestDataConstants.RIDE;
        String messageKey = RideExceptionMessageKeys.RIDE_STATUS_INCORRECT_MESSAGE;
        Object[] args = new Object[]{rideStatusRequest.rideStatus()};

        when(rideServiceValidation.findRideByIdWithCheck(rideId)).thenReturn(ride);
        doThrow(new RideStatusIncorrectException(messageKey, args))
            .when(rideServiceValidation).validChangeRideStatus(ride, rideStatusRequest);

        assertThatThrownBy(() -> rideService.updateRideStatus(rideStatusRequest, rideId))
            .isInstanceOf(RideStatusIncorrectException.class)
            .extracting("messageKey", "args")
            .containsExactly(messageKey, args);

        verify(rideServiceValidation).findRideByIdWithCheck(rideId);
        verify(rideServiceValidation).validChangeRideStatus(ride, rideStatusRequest);
        verify(rideRepository, never()).save(any(Ride.class));
    }

    @Test
    void getAllRides_ReturnsPageRideResponse_ValidInputArguments() {
        int offset = TestDataConstants.PAGE_OFFSET;
        int limit = TestDataConstants.PAGE_LIMIT;
        PageResponse<RideResponse> expectedResponse = PageResponse.<RideResponse>builder()
            .addCurrentOffset(offset)
            .addCurrentLimit(limit)
            .addTotalPages(1)
            .addTotalElements(1)
            .addSort("id")
            .addValues(Collections.singletonList(TestDataConstants.RIDE_RESPONSE))
            .build();
        List<Ride> rides = Collections.singletonList(TestDataConstants.RIDE);
        Page<Ride> ridePage = new PageImpl<>(rides);
        when(rideRepository.findAll(any(Pageable.class))).thenReturn(ridePage);
        arrangeForTestingPageMethods(expectedResponse);

        PageResponse<RideResponse> actual = rideService.getAllRides(offset, limit);

        assertThat(actual).isSameAs(expectedResponse);
        verify(rideMapper).toResponse(TestDataConstants.RIDE);
        verify(pageResponseMapper).toDto(ArgumentMatchers.<Page<RideResponse>>any());
    }

    @Test
    void getAllRidesByDriver_ReturnsPageRideResponse_ValidInputArguments() {
        int offset = TestDataConstants.PAGE_OFFSET;
        int limit = TestDataConstants.PAGE_LIMIT;
        Long driverId = TestDataConstants.RIDE.getDriverId();
        PageResponse<RideResponse> expectedResponse = PageResponse.<RideResponse>builder()
            .addCurrentOffset(offset)
            .addCurrentLimit(limit)
            .addTotalPages(1)
            .addTotalElements(1)
            .addSort("id")
            .addValues(Collections.singletonList(TestDataConstants.RIDE_RESPONSE))
            .build();
        List<Ride> rides = Collections.singletonList(TestDataConstants.RIDE);
        Page<Ride> ridePage = new PageImpl<>(rides);
        doNothing().when(rideServiceValidation).checkDriverExists(driverId, "en");
        when(rideRepository.findAllByDriverId(any(Pageable.class), eq(driverId))).thenReturn(ridePage);
        arrangeForTestingPageMethods(expectedResponse);

        PageResponse<RideResponse> actual = rideService.getAllRidesByDriver(offset, limit, driverId, "en");

        assertThat(actual).isSameAs(expectedResponse);
        verify(rideServiceValidation).checkDriverExists(driverId, "en");
        verify(rideMapper).toResponse(TestDataConstants.RIDE);
        verify(pageResponseMapper).toDto(ArgumentMatchers.<Page<RideResponse>>any());
    }

    @Test
    void getAllRidesByPassenger_ReturnsPageRideResponse_ValidInputArguments() {
        int offset = TestDataConstants.PAGE_OFFSET;
        int limit = TestDataConstants.PAGE_LIMIT;
        Long passengerId = TestDataConstants.RIDE.getPassengerId();
        PageResponse<RideResponse> expectedResponse = PageResponse.<RideResponse>builder()
            .addCurrentOffset(offset)
            .addCurrentLimit(limit)
            .addTotalPages(1)
            .addTotalElements(1)
            .addSort("id")
            .addValues(Collections.singletonList(TestDataConstants.RIDE_RESPONSE))
            .build();
        List<Ride> rides = Collections.singletonList(TestDataConstants.RIDE);
        Page<Ride> ridePage = new PageImpl<>(rides);
        doNothing().when(rideServiceValidation).checkPassengerExists(passengerId, "en");
        when(rideRepository.findAllByPassengerId(any(Pageable.class), eq(passengerId))).thenReturn(ridePage);
        arrangeForTestingPageMethods(expectedResponse);

        PageResponse<RideResponse> actual = rideService.getAllRidesByPassenger(offset, limit, passengerId, "en");

        assertThat(actual).isSameAs(expectedResponse);
        verify(rideServiceValidation).checkPassengerExists(passengerId, "en");
        verify(rideMapper).toResponse(TestDataConstants.RIDE);
        verify(pageResponseMapper).toDto(ArgumentMatchers.<Page<RideResponse>>any());
    }

    @Test
    void deleteRideById_MarksRideAsDeleted_ValidInputArguments() {
        Long rideId = TestDataConstants.RIDE_ID;

        doNothing().when(rideServiceValidation).checkRideExistsById(rideId);
        doNothing().when(rideRepository).deleteById(rideId);

        rideService.deleteRideById(rideId);

        verify(rideServiceValidation).checkRideExistsById(rideId);
        verify(rideRepository).deleteById(rideId);
    }

    @Test
    void deleteRideById_ThrowsException_RideNotFound() {
        Long rideId = TestDataConstants.RIDE_ID;
        String messageKey = RideExceptionMessageKeys.RIDE_NOT_FOUND_MESSAGE;
        Object[] args = new Object[]{rideId};

        doThrow(new RideNotFoundException(messageKey, args))
            .when(rideServiceValidation).checkRideExistsById(rideId);

        assertThatThrownBy(() -> rideService.deleteRideById(rideId))
            .isInstanceOf(RideNotFoundException.class)
            .extracting("messageKey", "args")
            .containsExactly(messageKey, args);

        verify(rideServiceValidation).checkRideExistsById(rideId);
        verify(rideRepository, never()).deleteById(any(Long.class));
    }

    @Test
    void getRideById_ReturnsRideResponse_ValidInputArguments() {
        Long rideId = TestDataConstants.RIDE_ID;
        Ride ride = TestDataConstants.RIDE;
        RideResponse expectedResponse = TestDataConstants.RIDE_RESPONSE;

        when(rideServiceValidation.findRideByIdWithCheck(rideId)).thenReturn(ride);
        when(rideMapper.toResponse(ride)).thenReturn(expectedResponse);

        RideResponse actual = rideService.getRideById(rideId);

        assertThat(actual).isEqualTo(expectedResponse);
        verify(rideServiceValidation).findRideByIdWithCheck(rideId);
        verify(rideMapper).toResponse(ride);
    }

    @Test
    void getRideById_ThrowsException_RideNotFound() {
        Long rideId = TestDataConstants.RIDE_ID;
        String messageKey = RideExceptionMessageKeys.RIDE_NOT_FOUND_MESSAGE;
        Object[] args = new Object[]{rideId};

        when(rideServiceValidation.findRideByIdWithCheck(rideId))
            .thenThrow(new RideNotFoundException(messageKey, args));

        assertThatThrownBy(() -> rideService.getRideById(rideId))
            .isInstanceOf(RideNotFoundException.class)
            .extracting("messageKey", "args")
            .containsExactly(messageKey, args);

        verify(rideServiceValidation).findRideByIdWithCheck(rideId);
        verify(rideMapper, never()).toResponse(any(Ride.class));
    }

    @Test
    void createRide_ThrowsException_DriverNotFound() {
        RideRequest rideRequest = TestDataConstants.RIDE_REQUEST;
        String messageKey = TestDataConstants.DRIVER_NOT_FOUND_MESSAGE;

        doThrow(new FeignClientException(new ExceptionDto(messageKey, HttpStatus.NOT_FOUND, LocalDateTime.now())))
            .when(rideServiceValidation).checkDriverExists(rideRequest.driverId(), "en");

        assertThatThrownBy(() -> rideService.createRide(rideRequest, "en"))
            .isInstanceOf(FeignClientException.class)
            .extracting("exceptionDto.message", "exceptionDto.status")
            .containsExactly(messageKey, HttpStatus.NOT_FOUND);

        verify(rideServiceValidation).checkDriverExists(rideRequest.driverId(), "en");
        verify(rideRepository, never()).save(any(Ride.class));
    }

    @Test
    void createRide_ThrowsException_PassengerNotFound() {
        RideRequest rideRequest = TestDataConstants.RIDE_REQUEST;
        String messageKey = TestDataConstants.PASSENGER_NOT_FOUND_MESSAGE;

        doThrow(new FeignClientException(new ExceptionDto(messageKey, HttpStatus.NOT_FOUND, LocalDateTime.now())))
            .when(rideServiceValidation).checkPassengerExists(rideRequest.passengerId(), "en");

        assertThatThrownBy(() -> rideService.createRide(rideRequest, "en"))
            .isInstanceOf(FeignClientException.class)
            .extracting("exceptionDto.message", "exceptionDto.status")
            .containsExactly(messageKey, HttpStatus.NOT_FOUND);

        verify(rideServiceValidation).checkPassengerExists(rideRequest.passengerId(), "en");
        verify(rideRepository, never()).save(any(Ride.class));
    }

    @Test
    void createRide_ThrowsException_FeignClientError() {
        RideRequest rideRequest = TestDataConstants.RIDE_REQUEST;
        String messageKey = TestDataConstants.FEIGN_CLIENT_ERROR_MESSAGE;

        doThrow(new FeignClientException(new ExceptionDto(messageKey, HttpStatus.INTERNAL_SERVER_ERROR, LocalDateTime.now())))
            .when(rideServiceValidation).checkDriverExists(rideRequest.driverId(), "en");

        assertThatThrownBy(() -> rideService.createRide(rideRequest, "en"))
            .isInstanceOf(FeignClientException.class)
            .extracting("exceptionDto.message", "exceptionDto.status")
            .containsExactly(messageKey, HttpStatus.INTERNAL_SERVER_ERROR);

        verify(rideServiceValidation).checkDriverExists(rideRequest.driverId(), "en");
        verify(rideRepository, never()).save(any(Ride.class));
    }

    @Test
    void getAllRidesByDriver_ThrowsException_DriverNotFound() {
        int offset = TestDataConstants.PAGE_OFFSET;
        int limit = TestDataConstants.PAGE_LIMIT;
        Long driverId = TestDataConstants.RIDE.getDriverId();
        String messageKey = TestDataConstants.DRIVER_NOT_FOUND_MESSAGE;

        doThrow(new FeignClientException(new ExceptionDto(messageKey, HttpStatus.NOT_FOUND, LocalDateTime.now())))
            .when(rideServiceValidation).checkDriverExists(driverId, "en");

        assertThatThrownBy(() -> rideService.getAllRidesByDriver(offset, limit, driverId, "en"))
            .isInstanceOf(FeignClientException.class)
            .extracting("exceptionDto.message", "exceptionDto.status")
            .containsExactly(messageKey, HttpStatus.NOT_FOUND);

        verify(rideServiceValidation).checkDriverExists(driverId, "en");
        verify(rideRepository, never()).findAllByDriverId(any(Pageable.class), eq(driverId));
    }

    @Test
    void getAllRidesByPassenger_ThrowsException_PassengerNotFound() {
        int offset = TestDataConstants.PAGE_OFFSET;
        int limit = TestDataConstants.PAGE_LIMIT;
        Long passengerId = TestDataConstants.RIDE.getPassengerId();
        String messageKey = TestDataConstants.PASSENGER_NOT_FOUND_MESSAGE;

        doThrow(new FeignClientException(new ExceptionDto(messageKey, HttpStatus.NOT_FOUND, LocalDateTime.now())))
            .when(rideServiceValidation).checkPassengerExists(passengerId, "en");

        assertThatThrownBy(() -> rideService.getAllRidesByPassenger(offset, limit, passengerId, "en"))
            .isInstanceOf(FeignClientException.class)
            .extracting("exceptionDto.message", "exceptionDto.status")
            .containsExactly(messageKey, HttpStatus.NOT_FOUND);

        verify(rideServiceValidation).checkPassengerExists(passengerId, "en");
        verify(rideRepository, never()).findAllByPassengerId(any(Pageable.class), eq(passengerId));
    }

    @Test
    void createRide_ThrowsException_FeignClientForbidden() {
        RideRequest rideRequest = TestDataConstants.RIDE_REQUEST;
        String messageKey = "Access denied";

        doThrow(new FeignClientException(new ExceptionDto(messageKey, HttpStatus.FORBIDDEN, LocalDateTime.now())))
            .when(rideServiceValidation).checkDriverExists(rideRequest.driverId(), "en");

        assertThatThrownBy(() -> rideService.createRide(rideRequest, "en"))
            .isInstanceOf(FeignClientException.class)
            .extracting("exceptionDto.message", "exceptionDto.status")
            .containsExactly(messageKey, HttpStatus.FORBIDDEN);

        verify(rideRepository, never()).save(any(Ride.class));
    }

    private void arrangeForTestingPageMethods(PageResponse<RideResponse> expectedResponse) {
        when(rideMapper.toResponse(any(Ride.class)))
            .thenReturn(TestDataConstants.RIDE_RESPONSE);
        when(pageResponseMapper.toDto(ArgumentMatchers.<Page<RideResponse>>any()))
            .thenReturn(expectedResponse);
    }

}