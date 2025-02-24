package by.modsen.passengerservice.service.component.validation.impl;

import by.modsen.passengerservice.TestDataConstants;
import by.modsen.passengerservice.constants.PassengerExceptionMessageKeys;
import by.modsen.passengerservice.dto.request.PassengerRequest;
import by.modsen.passengerservice.exception.passenger.PassengerAlreadyExistsException;
import by.modsen.passengerservice.exception.passenger.PassengerNotFoundException;
import by.modsen.passengerservice.model.Passenger;
import by.modsen.passengerservice.repository.PassengerRepository;
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
class PassengerServiceValidationImplTest {

    @Mock
    private PassengerRepository passengerRepository;

    @InjectMocks
    private PassengerServiceValidationImpl passengerServiceValidation;

    @Test
    void restoreOption_ThrowsException_PassengerWithEmailWasDeleted() {
        PassengerRequest passengerRequest = TestDataConstants.PASSENGER_REQUEST;
        String email = passengerRequest.email();
        String messageKey = PassengerExceptionMessageKeys.PASSENGER_RESTORE_EMAIL_OPTION_MESSAGE_KEY;
        Object[] args = new Object[]{email};

        when(passengerRepository.existsPassengerByEmailAndIsDeletedIsTrue(email))
            .thenReturn(true);

        assertThatThrownBy(() -> passengerServiceValidation.restoreOption(passengerRequest))
            .isInstanceOf(PassengerAlreadyExistsException.class)
            .extracting("messageKey", "args")
            .containsExactly(messageKey, args);

        verify(passengerRepository).existsPassengerByEmailAndIsDeletedIsTrue(email);
    }

    @Test
    void restoreOption_ThrowsException_PassengerWithPhoneWasDeleted() {
        PassengerRequest passengerRequest = TestDataConstants.PASSENGER_REQUEST;
        String phoneNumber = passengerRequest.phoneNumber();
        String messageKey = PassengerExceptionMessageKeys.PASSENGER_RESTORE_PHONE_OPTION_MESSAGE_KEY;
        Object[] args = new Object[]{phoneNumber};

        when(passengerRepository.existsPassengerByPhoneNumberAndIsDeletedIsTrue(phoneNumber))
            .thenReturn(true);

        assertThatThrownBy(() -> passengerServiceValidation.restoreOption(passengerRequest))
            .isInstanceOf(PassengerAlreadyExistsException.class)
            .extracting("messageKey", "args")
            .containsExactly(messageKey, args);

        verify(passengerRepository).existsPassengerByPhoneNumberAndIsDeletedIsTrue(phoneNumber);
    }

    @Test
    void restoreOption_NoException_NoPassengerToRestore() {
        PassengerRequest passengerRequest = TestDataConstants.PASSENGER_REQUEST;

        when(passengerRepository.existsPassengerByEmailAndIsDeletedIsTrue(passengerRequest.email()))
            .thenReturn(false);
        when(passengerRepository.existsPassengerByPhoneNumberAndIsDeletedIsTrue(passengerRequest.phoneNumber()))
            .thenReturn(false);

        assertThatCode(() -> passengerServiceValidation.restoreOption(passengerRequest))
            .doesNotThrowAnyException();

        verify(passengerRepository).existsPassengerByEmailAndIsDeletedIsTrue(passengerRequest.email());
        verify(passengerRepository).existsPassengerByPhoneNumberAndIsDeletedIsTrue(passengerRequest.phoneNumber());
    }

    @Test
    void checkAlreadyExists_ThrowsException_PassengerWithEmailAlreadyExists() {
        PassengerRequest passengerRequest = TestDataConstants.PASSENGER_REQUEST;
        String email = passengerRequest.email();
        String messageKey = PassengerExceptionMessageKeys.PASSENGER_EMAIL_ALREADY_EXISTS_MESSAGE_KEY;
        Object[] args = new Object[]{email};

        when(passengerRepository.existsPassengerByEmailAndIsDeletedIsFalse(email))
            .thenReturn(true);

        assertThatThrownBy(() -> passengerServiceValidation.checkAlreadyExists(passengerRequest))
            .isInstanceOf(PassengerAlreadyExistsException.class)
            .extracting("messageKey", "args")
            .containsExactly(messageKey, args);

        verify(passengerRepository).existsPassengerByEmailAndIsDeletedIsFalse(email);
    }

    @Test
    void checkAlreadyExists_ThrowsException_PassengerWithPhoneAlreadyExists() {
        PassengerRequest passengerRequest = TestDataConstants.PASSENGER_REQUEST;
        String phoneNumber = passengerRequest.phoneNumber();
        String messageKey = PassengerExceptionMessageKeys.PASSENGER_PHONE_NUMBER_ALREADY_EXISTS_MESSAGE_KEY;
        Object[] args = new Object[]{phoneNumber};

        when(passengerRepository.existsPassengerByPhoneNumberAndIsDeletedIsFalse(phoneNumber))
            .thenReturn(true);

        assertThatThrownBy(() -> passengerServiceValidation.checkAlreadyExists(passengerRequest))
            .isInstanceOf(PassengerAlreadyExistsException.class)
            .extracting("messageKey", "args")
            .containsExactly(messageKey, args);

        verify(passengerRepository).existsPassengerByPhoneNumberAndIsDeletedIsFalse(phoneNumber);
    }

    @Test
    void checkAlreadyExists_NoException_NoPassengerExists() {
        PassengerRequest passengerRequest = TestDataConstants.PASSENGER_REQUEST;

        when(passengerRepository.existsPassengerByEmailAndIsDeletedIsFalse(passengerRequest.email()))
            .thenReturn(false);
        when(passengerRepository.existsPassengerByPhoneNumberAndIsDeletedIsFalse(passengerRequest.phoneNumber()))
            .thenReturn(false);

        assertThatCode(() -> passengerServiceValidation.checkAlreadyExists(passengerRequest))
            .doesNotThrowAnyException();

        verify(passengerRepository).existsPassengerByEmailAndIsDeletedIsFalse(passengerRequest.email());
        verify(passengerRepository).existsPassengerByPhoneNumberAndIsDeletedIsFalse(passengerRequest.phoneNumber());
    }

    @Test
    void findPassengerByIdWithChecks_ReturnsPassenger_ValidInputArguments() {
        Long passengerId = TestDataConstants.PASSENGER_ID;
        Passenger passenger = TestDataConstants.PASSENGER;

        when(passengerRepository.findPassengerByIdAndIsDeletedIsFalse(passengerId))
            .thenReturn(Optional.of(passenger));

        Passenger result = passengerServiceValidation.findPassengerByIdWithChecks(passengerId);

        assertThat(result).isEqualTo(passenger);
        verify(passengerRepository).findPassengerByIdAndIsDeletedIsFalse(passengerId);
    }

    @Test
    void findPassengerByIdWithChecks_ThrowsException_PassengerNotFound() {
        Long passengerId = TestDataConstants.PASSENGER_ID;
        String messageKey = PassengerExceptionMessageKeys.PASSENGER_NOT_FOUND_MESSAGE_KEY;
        Object[] args = new Object[]{passengerId};

        when(passengerRepository.findPassengerByIdAndIsDeletedIsFalse(passengerId))
            .thenReturn(Optional.empty());

        assertThatThrownBy(() -> passengerServiceValidation.findPassengerByIdWithChecks(passengerId))
            .isInstanceOf(PassengerNotFoundException.class)
            .extracting("messageKey", "args")
            .containsExactly(messageKey, args);

        verify(passengerRepository).findPassengerByIdAndIsDeletedIsFalse(passengerId);
    }

}