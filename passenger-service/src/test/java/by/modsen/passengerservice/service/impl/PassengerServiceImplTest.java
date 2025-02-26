package by.modsen.passengerservice.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import by.modsen.passengerservice.constants.TestDataConstants;
import by.modsen.passengerservice.constants.PassengerExceptionMessageKeys;
import by.modsen.passengerservice.dto.request.PassengerRequest;
import by.modsen.passengerservice.dto.response.PageResponse;
import by.modsen.passengerservice.dto.response.PassengerResponse;
import by.modsen.passengerservice.exception.passenger.PassengerAlreadyExistsException;
import by.modsen.passengerservice.exception.passenger.PassengerNotFoundException;
import by.modsen.passengerservice.mapper.PageResponseMapper;
import by.modsen.passengerservice.mapper.PassengerMapper;
import by.modsen.passengerservice.model.Passenger;
import by.modsen.passengerservice.repository.PassengerRepository;
import by.modsen.passengerservice.service.component.validation.PassengerServiceValidation;
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
class PassengerServiceImplTest {

    @Mock
    private PassengerRepository passengerRepository;

    @Mock
    private PassengerMapper passengerMapper;

    @Mock
    private PageResponseMapper pageResponseMapper;

    @Mock
    private PassengerServiceValidation passengerServiceValidation;

    @InjectMocks
    private PassengerServiceImpl passengerService;

    @Test
    void getAllPassengers_ReturnsPagePassengerResponse_ValidInputArguments() {
        int offset = TestDataConstants.PAGE_OFFSET;
        int limit = TestDataConstants.PAGE_LIMIT;
        PageResponse<PassengerResponse> expectedResponse = PageResponse.<PassengerResponse>builder()
            .addCurrentOffset(offset)
            .addCurrentLimit(limit)
            .addTotalPages(1)
            .addTotalElements(1)
            .addSort("id")
            .addValues(Collections.singletonList(TestDataConstants.PASSENGER_RESPONSE))
            .build();
        List<Passenger> passengers = Collections.singletonList(TestDataConstants.PASSENGER);
        Page<Passenger> passengerPage = new PageImpl<>(passengers);
        when(passengerRepository.findAllByIsDeletedIsFalse(any(Pageable.class)))
            .thenReturn(passengerPage);
        arrangeForTestingPageMethods(expectedResponse);

        PageResponse<PassengerResponse> actual = passengerService.getAllPassengers(offset, limit);

        assertThat(actual).isSameAs(expectedResponse);
        verify(passengerMapper).toResponse(TestDataConstants.PASSENGER);
        verify(pageResponseMapper).toDto(ArgumentMatchers.<Page<PassengerResponse>>any());
    }

    @Test
    void createPassenger_ReturnsPassengerResponse_ValidInputArguments() {
        PassengerRequest passengerRequest = TestDataConstants.PASSENGER_REQUEST;
        Passenger passenger = TestDataConstants.PASSENGER;
        PassengerResponse expectedResponse = TestDataConstants.PASSENGER_RESPONSE;

        doNothing().when(passengerServiceValidation).restoreOption(passengerRequest);
        doNothing().when(passengerServiceValidation).checkAlreadyExists(passengerRequest);

        when(passengerMapper.toEntity(passengerRequest)).thenReturn(passenger);
        when(passengerRepository.save(passenger)).thenReturn(passenger);
        when(passengerMapper.toResponse(passenger)).thenReturn(expectedResponse);

        PassengerResponse actual = passengerService.createPassenger(passengerRequest);

        assertThat(actual).isEqualTo(expectedResponse);
        verify(passengerServiceValidation).restoreOption(passengerRequest);
        verify(passengerServiceValidation).checkAlreadyExists(passengerRequest);
        verify(passengerMapper).toEntity(passengerRequest);
        verify(passengerRepository).save(passenger);
        verify(passengerMapper).toResponse(passenger);
    }

    @Test
    void createPassenger_ThrowsException_PassengerWithEmailAlreadyExists() {
        PassengerRequest passengerRequest = TestDataConstants.PASSENGER_REQUEST;
        String messageKey = PassengerExceptionMessageKeys.PASSENGER_EMAIL_ALREADY_EXISTS_MESSAGE_KEY;
        Object[] args = new Object[]{passengerRequest.email()};

        doThrow(new PassengerAlreadyExistsException(messageKey, args))
            .when(passengerServiceValidation).checkAlreadyExists(passengerRequest);

        assertThatThrownBy(() -> passengerService.createPassenger(passengerRequest))
            .isInstanceOf(PassengerAlreadyExistsException.class)
            .extracting("messageKey", "args")
            .containsExactly(messageKey, args);

        verify(passengerServiceValidation).checkAlreadyExists(passengerRequest);
        verify(passengerRepository, never()).save(any(Passenger.class));
    }

    @Test
    void createPassenger_ThrowsException_PassengerWithPhoneAlreadyExists() {
        PassengerRequest passengerRequest = TestDataConstants.PASSENGER_REQUEST;
        String messageKey = PassengerExceptionMessageKeys.PASSENGER_PHONE_NUMBER_ALREADY_EXISTS_MESSAGE_KEY;
        Object[] args = new Object[]{passengerRequest.phoneNumber()};

        doThrow(new PassengerAlreadyExistsException(messageKey, args))
            .when(passengerServiceValidation).checkAlreadyExists(passengerRequest);

        assertThatThrownBy(() -> passengerService.createPassenger(passengerRequest))
            .isInstanceOf(PassengerAlreadyExistsException.class)
            .extracting("messageKey", "args")
            .containsExactly(messageKey, args);

        verify(passengerServiceValidation).checkAlreadyExists(passengerRequest);
        verify(passengerRepository, never()).save(any(Passenger.class));
    }

    @Test
    void createPassenger_ThrowsException_PassengerWithEmailWasDeleted() {
        PassengerRequest passengerRequest = TestDataConstants.PASSENGER_REQUEST;
        String messageKey = PassengerExceptionMessageKeys.PASSENGER_RESTORE_EMAIL_OPTION_MESSAGE_KEY;
        Object[] args = new Object[]{passengerRequest.email()};

        doThrow(new PassengerAlreadyExistsException(messageKey, args))
            .when(passengerServiceValidation).restoreOption(passengerRequest);

        assertThatThrownBy(() -> passengerService.createPassenger(passengerRequest))
            .isInstanceOf(PassengerAlreadyExistsException.class)
            .extracting("messageKey", "args")
            .containsExactly(messageKey, args);

        verify(passengerServiceValidation).restoreOption(passengerRequest);
        verify(passengerRepository, never()).save(any(Passenger.class));
    }

    @Test
    void createPassenger_ThrowsException_PassengerWithPhoneWasDeleted() {
        PassengerRequest passengerRequest = TestDataConstants.PASSENGER_REQUEST;
        String messageKey = PassengerExceptionMessageKeys.PASSENGER_RESTORE_PHONE_OPTION_MESSAGE_KEY;
        Object[] args = new Object[]{passengerRequest.phoneNumber()};

        doThrow(new PassengerAlreadyExistsException(messageKey, args))
            .when(passengerServiceValidation).restoreOption(passengerRequest);

        assertThatThrownBy(() -> passengerService.createPassenger(passengerRequest))
            .isInstanceOf(PassengerAlreadyExistsException.class)
            .extracting("messageKey", "args")
            .containsExactly(messageKey, args);

        verify(passengerServiceValidation).restoreOption(passengerRequest);
        verify(passengerRepository, never()).save(any(Passenger.class));
    }

    @Test
    void updatePassengerById_ReturnsPassengerResponse_ValidInputArguments() {
        PassengerRequest passengerRequest = TestDataConstants.PASSENGER_REQUEST;
        Long passengerId = TestDataConstants.PASSENGER_ID;
        Passenger existingPassenger = TestDataConstants.PASSENGER;
        Passenger updatedPassenger = TestDataConstants.PASSENGER;
        PassengerResponse expectedResponse = TestDataConstants.PASSENGER_RESPONSE;

        doNothing().when(passengerServiceValidation).restoreOption(passengerRequest);
        doNothing().when(passengerServiceValidation).checkAlreadyExists(passengerRequest);
        when(passengerServiceValidation.findPassengerByIdWithChecks(passengerId)).thenReturn(existingPassenger);
        when(passengerRepository.save(existingPassenger)).thenReturn(updatedPassenger);
        when(passengerMapper.toResponse(updatedPassenger)).thenReturn(expectedResponse);

        PassengerResponse actual = passengerService.updatePassengerById(passengerRequest, passengerId);

        assertThat(actual).isEqualTo(expectedResponse);
        verify(passengerServiceValidation).restoreOption(passengerRequest);
        verify(passengerServiceValidation).checkAlreadyExists(passengerRequest);
        verify(passengerServiceValidation).findPassengerByIdWithChecks(passengerId);
        verify(passengerMapper).updatePassengerFromDto(passengerRequest, existingPassenger);
        verify(passengerRepository).save(existingPassenger);
        verify(passengerMapper).toResponse(updatedPassenger);
    }

    @Test
    void updatePassengerById_ThrowsException_PassengerNotFound() {
        PassengerRequest passengerRequest = TestDataConstants.PASSENGER_REQUEST;
        Long passengerId = TestDataConstants.PASSENGER_ID;
        String messageKey = PassengerExceptionMessageKeys.PASSENGER_NOT_FOUND_MESSAGE_KEY;
        Object[] args = new Object[]{passengerId};

        when(passengerServiceValidation.findPassengerByIdWithChecks(passengerId))
            .thenThrow(new PassengerNotFoundException(messageKey, args));

        assertThatThrownBy(() -> passengerService.updatePassengerById(passengerRequest, passengerId))
            .isInstanceOf(PassengerNotFoundException.class)
            .extracting("messageKey", "args")
            .containsExactly(messageKey, args);

        verify(passengerServiceValidation).findPassengerByIdWithChecks(passengerId);
        verify(passengerRepository, never()).save(any(Passenger.class));
    }

    @Test
    void updatePassengerById_ThrowsException_PassengerWithEmailAlreadyExists() {
        PassengerRequest passengerRequest = TestDataConstants.PASSENGER_REQUEST;
        Long passengerId = TestDataConstants.PASSENGER_ID;
        Passenger existingPassenger = TestDataConstants.PASSENGER;
        String messageKey = PassengerExceptionMessageKeys.PASSENGER_EMAIL_ALREADY_EXISTS_MESSAGE_KEY;
        Object[] args = new Object[]{passengerRequest.email()};

        doThrow(new PassengerAlreadyExistsException(messageKey, args))
            .when(passengerServiceValidation).checkAlreadyExists(passengerRequest);

        lenient().when(passengerServiceValidation.findPassengerByIdWithChecks(passengerId))
            .thenReturn(existingPassenger);

        assertThatThrownBy(() -> passengerService.updatePassengerById(passengerRequest, passengerId))
            .isInstanceOf(PassengerAlreadyExistsException.class)
            .extracting("messageKey", "args")
            .containsExactly(messageKey, args);

        verify(passengerServiceValidation).checkAlreadyExists(passengerRequest);
        verify(passengerRepository, never()).save(any(Passenger.class));
    }

    @Test
    void updatePassengerById_ThrowsException_PassengerWithPhoneAlreadyExists() {
        PassengerRequest passengerRequest = TestDataConstants.PASSENGER_REQUEST;
        Long passengerId = TestDataConstants.PASSENGER_ID;
        Passenger existingPassenger = TestDataConstants.PASSENGER;
        String messageKey = PassengerExceptionMessageKeys.PASSENGER_PHONE_NUMBER_ALREADY_EXISTS_MESSAGE_KEY;
        Object[] args = new Object[]{passengerRequest.phoneNumber()};

        doThrow(new PassengerAlreadyExistsException(messageKey, args))
            .when(passengerServiceValidation).checkAlreadyExists(passengerRequest);

        lenient().when(passengerServiceValidation.findPassengerByIdWithChecks(passengerId))
            .thenReturn(existingPassenger);

        assertThatThrownBy(() -> passengerService.updatePassengerById(passengerRequest, passengerId))
            .isInstanceOf(PassengerAlreadyExistsException.class)
            .extracting("messageKey", "args")
            .containsExactly(messageKey, args);

        verify(passengerServiceValidation).checkAlreadyExists(passengerRequest);
        verify(passengerRepository, never()).save(any(Passenger.class));
    }

    @Test
    void updatePassengerById_ThrowsException_PassengerWithEmailWasDeleted() {
        PassengerRequest passengerRequest = TestDataConstants.PASSENGER_REQUEST;
        Long passengerId = TestDataConstants.PASSENGER_ID;
        String messageKey = PassengerExceptionMessageKeys.PASSENGER_RESTORE_EMAIL_OPTION_MESSAGE_KEY;
        Object[] args = new Object[]{passengerRequest.email()};

        doThrow(new PassengerAlreadyExistsException(messageKey, args))
            .when(passengerServiceValidation).restoreOption(passengerRequest);

        assertThatThrownBy(() -> passengerService.updatePassengerById(passengerRequest, passengerId))
            .isInstanceOf(PassengerAlreadyExistsException.class)
            .extracting("messageKey", "args")
            .containsExactly(messageKey, args);

        verify(passengerServiceValidation).restoreOption(passengerRequest);
        verify(passengerRepository, never()).save(any(Passenger.class));
    }

    @Test
    void updatePassengerById_ThrowsException_PassengerWithPhoneWasDeleted() {
        PassengerRequest passengerRequest = TestDataConstants.PASSENGER_REQUEST;
        Long passengerId = TestDataConstants.PASSENGER_ID;
        String messageKey = PassengerExceptionMessageKeys.PASSENGER_RESTORE_PHONE_OPTION_MESSAGE_KEY;
        Object[] args = new Object[]{passengerRequest.phoneNumber()};

        doThrow(new PassengerAlreadyExistsException(messageKey, args))
            .when(passengerServiceValidation).restoreOption(passengerRequest);

        assertThatThrownBy(() -> passengerService.updatePassengerById(passengerRequest, passengerId))
            .isInstanceOf(PassengerAlreadyExistsException.class)
            .extracting("messageKey", "args")
            .containsExactly(messageKey, args);

        verify(passengerServiceValidation).restoreOption(passengerRequest);
        verify(passengerRepository, never()).save(any(Passenger.class));
    }

    @Test
    void deletePassengerById_MarksPassengerAsDeleted_ValidInputArguments() {
        Long passengerId = TestDataConstants.PASSENGER_ID;
        Passenger passenger = TestDataConstants.PASSENGER;

        when(passengerServiceValidation.findPassengerByIdWithChecks(passengerId))
            .thenReturn(passenger);
        when(passengerRepository.save(passenger)).thenReturn(passenger);

        passengerService.deletePassengerById(passengerId);

        assertThat(passenger.getIsDeleted()).isTrue();
        verify(passengerServiceValidation).findPassengerByIdWithChecks(passengerId);
        verify(passengerRepository).save(passenger);
    }

    @Test
    void deletePassengerById_ThrowsException_PassengerNotFound() {
        Long passengerId = TestDataConstants.PASSENGER_ID;
        String messageKey = PassengerExceptionMessageKeys.PASSENGER_NOT_FOUND_MESSAGE_KEY;
        Object[] args = new Object[]{passengerId};

        when(passengerServiceValidation.findPassengerByIdWithChecks(passengerId))
            .thenThrow(new PassengerNotFoundException(messageKey, args));

        assertThatThrownBy(() -> passengerService.deletePassengerById(passengerId))
            .isInstanceOf(PassengerNotFoundException.class)
            .extracting("messageKey", "args")
            .containsExactly(messageKey, args);

        verify(passengerServiceValidation).findPassengerByIdWithChecks(passengerId);
        verify(passengerRepository, never()).save(any(Passenger.class));
    }

    @Test
    void getPassengerById_ReturnsPassengerResponse_ValidInputArguments() {
        Long passengerId = TestDataConstants.PASSENGER_ID;
        Passenger passenger = TestDataConstants.PASSENGER;
        PassengerResponse expectedResponse = TestDataConstants.PASSENGER_RESPONSE;

        when(passengerServiceValidation.findPassengerByIdWithChecks(passengerId))
            .thenReturn(passenger);
        when(passengerMapper.toResponse(passenger))
            .thenReturn(expectedResponse);

        PassengerResponse actual = passengerService.getPassengerById(passengerId);

        assertThat(actual).isEqualTo(expectedResponse);
        verify(passengerServiceValidation).findPassengerByIdWithChecks(passengerId);
        verify(passengerMapper).toResponse(passenger);
    }

    @Test
    void getPassengerById_ThrowsException_PassengerNotFound() {
        Long passengerId = TestDataConstants.PASSENGER_ID;
        String messageKey = PassengerExceptionMessageKeys.PASSENGER_NOT_FOUND_MESSAGE_KEY;
        Object[] args = new Object[]{passengerId};

        when(passengerServiceValidation.findPassengerByIdWithChecks(passengerId))
            .thenThrow(new PassengerNotFoundException(messageKey, args));

        assertThatThrownBy(() -> passengerService.getPassengerById(passengerId))
            .isInstanceOf(PassengerNotFoundException.class)
            .extracting("messageKey", "args")
            .containsExactly(messageKey, args);

        verify(passengerServiceValidation).findPassengerByIdWithChecks(passengerId);
        verify(passengerMapper, never()).toResponse(any(Passenger.class));
    }

    private void arrangeForTestingPageMethods(PageResponse<PassengerResponse> expectedResponse) {
        when(passengerMapper.toResponse(any(Passenger.class)))
            .thenReturn(TestDataConstants.PASSENGER_RESPONSE);
        when(pageResponseMapper.toDto(ArgumentMatchers.<Page<PassengerResponse>>any()))
            .thenReturn(expectedResponse);
    }

}
