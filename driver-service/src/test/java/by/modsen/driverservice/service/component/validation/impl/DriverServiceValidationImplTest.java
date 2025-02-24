package by.modsen.driverservice.service.component.validation.impl;

import by.modsen.driverservice.constants.TestDataConstants;
import by.modsen.driverservice.constants.DriverExceptionMessageKeys;
import by.modsen.driverservice.dto.request.DriverRequest;
import by.modsen.driverservice.exception.driver.DriverAlreadyExistsException;
import by.modsen.driverservice.exception.driver.DriverNotFoundException;
import by.modsen.driverservice.model.Driver;
import by.modsen.driverservice.repository.DriverRepository;
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
class DriverServiceValidationImplTest {

    @Mock
    private DriverRepository driverRepository;

    @InjectMocks
    private DriverServiceValidationImpl driverServiceValidation;

    @Test
    void restoreOption_ThrowsException_DriverWithEmailWasDeleted() {
        DriverRequest driverRequest = TestDataConstants.DRIVER_REQUEST;
        String email = driverRequest.email();
        String messageKey = DriverExceptionMessageKeys.DRIVER_RESTORE_EMAIL_OPTION_MESSAGE_KEY;
        Object[] args = new Object[]{email};

        when(driverRepository.existsDriverByEmailAndIsDeletedIsTrue(email))
            .thenReturn(true);

        assertThatThrownBy(() -> driverServiceValidation.restoreOption(driverRequest))
            .isInstanceOf(DriverAlreadyExistsException.class)
            .extracting("messageKey", "args")
            .containsExactly(messageKey, args);

        verify(driverRepository).existsDriverByEmailAndIsDeletedIsTrue(email);
    }

    @Test
    void restoreOption_ThrowsException_DriverWithPhoneWasDeleted() {
        DriverRequest driverRequest = TestDataConstants.DRIVER_REQUEST;
        String phoneNumber = driverRequest.phoneNumber();
        String messageKey = DriverExceptionMessageKeys.DRIVER_RESTORE_PHONE_OPTION_MESSAGE_KEY;
        Object[] args = new Object[]{phoneNumber};

        when(driverRepository.existsDriverByPhoneNumberAndIsDeletedIsTrue(phoneNumber))
            .thenReturn(true);

        assertThatThrownBy(() -> driverServiceValidation.restoreOption(driverRequest))
            .isInstanceOf(DriverAlreadyExistsException.class)
            .extracting("messageKey", "args")
            .containsExactly(messageKey, args);

        verify(driverRepository).existsDriverByPhoneNumberAndIsDeletedIsTrue(phoneNumber);
    }

    @Test
    void restoreOption_NoException_NoDriverToRestore() {
        DriverRequest driverRequest = TestDataConstants.DRIVER_REQUEST;

        when(driverRepository.existsDriverByEmailAndIsDeletedIsTrue(driverRequest.email()))
            .thenReturn(false);
        when(driverRepository.existsDriverByPhoneNumberAndIsDeletedIsTrue(driverRequest.phoneNumber()))
            .thenReturn(false);

        assertThatCode(() -> driverServiceValidation.restoreOption(driverRequest))
            .doesNotThrowAnyException();

        verify(driverRepository).existsDriverByEmailAndIsDeletedIsTrue(driverRequest.email());
        verify(driverRepository).existsDriverByPhoneNumberAndIsDeletedIsTrue(driverRequest.phoneNumber());
    }

    @Test
    void checkAlreadyExists_ThrowsException_DriverWithEmailAlreadyExists() {
        DriverRequest driverRequest = TestDataConstants.DRIVER_REQUEST;
        String email = driverRequest.email();
        String messageKey = DriverExceptionMessageKeys.DRIVER_ALREADY_EXISTS_BY_EMAIL_MESSAGE_KEY;
        Object[] args = new Object[]{email};

        when(driverRepository.existsDriverByEmailAndIsDeletedIsFalse(email))
            .thenReturn(true);

        assertThatThrownBy(() -> driverServiceValidation.checkAlreadyExists(driverRequest))
            .isInstanceOf(DriverAlreadyExistsException.class)
            .extracting("messageKey", "args")
            .containsExactly(messageKey, args);

        verify(driverRepository).existsDriverByEmailAndIsDeletedIsFalse(email);
    }

    @Test
    void checkAlreadyExists_ThrowsException_DriverWithPhoneAlreadyExists() {
        DriverRequest driverRequest = TestDataConstants.DRIVER_REQUEST;
        String phoneNumber = driverRequest.phoneNumber();
        String messageKey = DriverExceptionMessageKeys.DRIVER_ALREADY_EXISTS_BY_PHONE_KEY;
        Object[] args = new Object[]{phoneNumber};

        when(driverRepository.existsDriverByPhoneNumberAndIsDeletedIsFalse(phoneNumber))
            .thenReturn(true);

        assertThatThrownBy(() -> driverServiceValidation.checkAlreadyExists(driverRequest))
            .isInstanceOf(DriverAlreadyExistsException.class)
            .extracting("messageKey", "args")
            .containsExactly(messageKey, args);

        verify(driverRepository).existsDriverByPhoneNumberAndIsDeletedIsFalse(phoneNumber);
    }

    @Test
    void checkAlreadyExists_NoException_NoDriverExists() {
        DriverRequest driverRequest = TestDataConstants.DRIVER_REQUEST;

        when(driverRepository.existsDriverByEmailAndIsDeletedIsFalse(driverRequest.email()))
            .thenReturn(false);
        when(driverRepository.existsDriverByPhoneNumberAndIsDeletedIsFalse(driverRequest.phoneNumber()))
            .thenReturn(false);

        assertThatCode(() -> driverServiceValidation.checkAlreadyExists(driverRequest))
            .doesNotThrowAnyException();

        verify(driverRepository).existsDriverByEmailAndIsDeletedIsFalse(driverRequest.email());
        verify(driverRepository).existsDriverByPhoneNumberAndIsDeletedIsFalse(driverRequest.phoneNumber());
    }

    @Test
    void findDriverByIdWithCheck_ReturnsDriver_ValidInputArguments() {
        Long driverId = TestDataConstants.DRIVER_ID;
        Driver driver = TestDataConstants.DRIVER;

        when(driverRepository.findDriverByIdAndIsDeletedIsFalse(driverId))
            .thenReturn(Optional.of(driver));

        Driver result = driverServiceValidation.findDriverByIdWithCheck(driverId);

        assertThat(result).isEqualTo(driver);
        verify(driverRepository).findDriverByIdAndIsDeletedIsFalse(driverId);
    }

    @Test
    void findDriverByIdWithCheck_ThrowsException_DriverNotFound() {
        Long driverId = TestDataConstants.DRIVER_ID;
        String messageKey = DriverExceptionMessageKeys.DRIVER_NOT_FOUND_MESSAGE_KEY;
        Object[] args = new Object[]{driverId};

        when(driverRepository.findDriverByIdAndIsDeletedIsFalse(driverId))
            .thenReturn(Optional.empty());

        assertThatThrownBy(() -> driverServiceValidation.findDriverByIdWithCheck(driverId))
            .isInstanceOf(DriverNotFoundException.class)
            .extracting("messageKey", "args")
            .containsExactly(messageKey, args);

        verify(driverRepository).findDriverByIdAndIsDeletedIsFalse(driverId);
    }
}