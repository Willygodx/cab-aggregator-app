package by.modsen.passengerservice.service.component.validation.impl;

import by.modsen.passengerservice.constants.PassengerExceptionMessageKeys;
import by.modsen.passengerservice.dto.request.PassengerRequest;
import by.modsen.passengerservice.exception.passenger.PassengerAlreadyExistsException;
import by.modsen.passengerservice.exception.passenger.PassengerNotFoundException;
import by.modsen.passengerservice.model.Passenger;
import by.modsen.passengerservice.repository.PassengerRepository;
import by.modsen.passengerservice.service.component.validation.PassengerServiceValidation;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PassengerServiceValidationImpl implements PassengerServiceValidation {

    private final MessageSource messageSource;
    private final PassengerRepository passengerRepository;

    public void restoreOption(PassengerRequest passengerRequest) {
        String email = passengerRequest.email();
        String phoneNumber = passengerRequest.phoneNumber();

        if (Objects.nonNull(email)
            && passengerRepository.existsPassengerByEmailAndIsDeletedIsTrue(email)) {
            throw new PassengerAlreadyExistsException(messageSource.getMessage(
                PassengerExceptionMessageKeys.PASSENGER_RESTORE_EMAIL_OPTION_MESSAGE_KEY,
                new Object[] {email},
                LocaleContextHolder.getLocale()
            ));
        }

        if (Objects.nonNull(phoneNumber)
            && passengerRepository.existsPassengerByPhoneNumberAndIsDeletedIsTrue(phoneNumber)) {
            throw new PassengerAlreadyExistsException(messageSource.getMessage(
                PassengerExceptionMessageKeys.PASSENGER_RESTORE_PHONE_OPTION_MESSAGE_KEY,
                new Object[] {phoneNumber},
                LocaleContextHolder.getLocale()
            ));
        }
    }

    public void checkAlreadyExists(PassengerRequest passengerRequest) {
        String email = passengerRequest.email();
        String phoneNumber = passengerRequest.phoneNumber();

        if (passengerRepository.existsPassengerByEmailAndIsDeletedIsFalse(email)) {
            throw new PassengerAlreadyExistsException(messageSource.getMessage(
                PassengerExceptionMessageKeys.PASSENGER_EMAIL_ALREADY_EXISTS_MESSAGE_KEY,
                new Object[] {email},
                LocaleContextHolder.getLocale()
            ));
        }

        if (passengerRepository.existsPassengerByPhoneNumberAndIsDeletedIsFalse(phoneNumber)) {
            throw new PassengerAlreadyExistsException(messageSource.getMessage(
                PassengerExceptionMessageKeys.PASSENGER_PHONE_NUMBER_ALREADY_EXISTS_MESSAGE_KEY,
                new Object[] {phoneNumber},
                LocaleContextHolder.getLocale()
            ));
        }
    }

    public Passenger findPassengerByIdWithChecks(Long passengerId) {
        return passengerRepository.findPassengerByIdAndIsDeletedIsFalse(passengerId)
            .orElseThrow(() -> new PassengerNotFoundException(messageSource.getMessage(
                PassengerExceptionMessageKeys.PASSENGER_NOT_FOUND_MESSAGE_KEY,
                new Object[] {passengerId},
                LocaleContextHolder.getLocale()
            )));
    }

}
