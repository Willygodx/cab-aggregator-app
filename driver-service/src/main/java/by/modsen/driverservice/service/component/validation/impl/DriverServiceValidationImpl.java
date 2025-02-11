package by.modsen.driverservice.service.component.validation.impl;

import by.modsen.driverservice.constants.DriverExceptionMessageKeys;
import by.modsen.driverservice.dto.request.DriverRequest;
import by.modsen.driverservice.exception.driver.DriverAlreadyExistsException;
import by.modsen.driverservice.exception.driver.DriverNotFoundException;
import by.modsen.driverservice.model.Driver;
import by.modsen.driverservice.repository.DriverRepository;
import by.modsen.driverservice.service.component.validation.DriverServiceValidation;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DriverServiceValidationImpl implements DriverServiceValidation {

    private final MessageSource messageSource;
    private final DriverRepository driverRepository;

    public void restoreOption(DriverRequest driverRequest) {
        String email = driverRequest.email();
        String phoneNumber = driverRequest.phoneNumber();

        if (Objects.nonNull(email) && driverRepository.existsDriverByEmailAndIsDeletedIsTrue(email)) {
            throw new DriverAlreadyExistsException(messageSource.getMessage(
                DriverExceptionMessageKeys.DRIVER_RESTORE_EMAIL_OPTION_MESSAGE_KEY,
                new Object[] {email},
                LocaleContextHolder.getLocale()
            ));
        }

        if (Objects.nonNull(phoneNumber) && driverRepository.existsDriverByPhoneNumberAndIsDeletedIsTrue(phoneNumber)) {
            throw new DriverAlreadyExistsException(messageSource.getMessage(
                DriverExceptionMessageKeys.DRIVER_RESTORE_PHONE_OPTION_MESSAGE_KEY,
                new Object[] {phoneNumber},
                LocaleContextHolder.getLocale()
            ));
        }
    }

    public void checkAlreadyExists(DriverRequest driverRequest) {
        String email = driverRequest.email();
        String phoneNumber = driverRequest.phoneNumber();

        if (driverRepository.existsDriverByEmailAndIsDeletedIsFalse(email)) {
            throw new DriverAlreadyExistsException(messageSource.getMessage(
                DriverExceptionMessageKeys.DRIVER_ALREADY_EXISTS_BY_EMAIL_MESSAGE_KEY,
                new Object[] {email},
                LocaleContextHolder.getLocale()
            ));
        }

        if (driverRepository.existsDriverByPhoneNumberAndIsDeletedIsFalse(phoneNumber)) {
            throw new DriverAlreadyExistsException(messageSource.getMessage(
                DriverExceptionMessageKeys.DRIVER_ALREADY_EXISTS_BY_PHONE_KEY,
                new Object[] {phoneNumber},
                LocaleContextHolder.getLocale()
            ));
        }
    }

    public Driver findDriverByIdWithCheck(Long id) {
        return driverRepository.findDriverByIdAndIsDeletedIsFalse(id)
            .orElseThrow(() -> new DriverNotFoundException(messageSource.getMessage(
                DriverExceptionMessageKeys.DRIVER_NOT_FOUND_MESSAGE_KEY,
                new Object[] {id},
                LocaleContextHolder.getLocale()
            )));
    }

}
