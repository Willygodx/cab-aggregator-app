package by.modsen.passengerservice.service.component.validation;

import by.modsen.passengerservice.constants.PassengerExceptionMessages;
import by.modsen.passengerservice.dto.PassengerDto;
import by.modsen.passengerservice.exception.passenger.PassengerAlreadyExistsException;
import by.modsen.passengerservice.exception.passenger.PassengerNotFoundException;
import by.modsen.passengerservice.model.Passenger;
import by.modsen.passengerservice.repository.PassengerRepository;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PassengerServiceValidation {

  private final MessageSource messageSource;
  private final PassengerRepository passengerRepository;

  public void restoreOption(PassengerDto passengerDto) {
    String email = passengerDto.email();
    String phoneNumber = passengerDto.phoneNumber();

    if (Objects.nonNull(email) &&
        passengerRepository.existsPassengerByEmailAndIsDeletedIsTrue(email)) {
      throw new PassengerAlreadyExistsException(messageSource.getMessage(
          PassengerExceptionMessages.PASSENGER_RESTORE_EMAIL_OPTION_MESSAGE,
          new Object[] {email},
          LocaleContextHolder.getLocale()
      ));
    }

    if (Objects.nonNull(phoneNumber) &&
        passengerRepository.existsPassengerByPhoneNumberAndIsDeletedIsTrue(phoneNumber)) {
      throw new PassengerAlreadyExistsException(messageSource.getMessage(
          PassengerExceptionMessages.PASSENGER_RESTORE_PHONE_OPTION_MESSAGE,
          new Object[] {phoneNumber},
          LocaleContextHolder.getLocale()
      ));
    }
  }

  public void checkAlreadyExists(PassengerDto passengerDto) {
    if (passengerRepository.existsPassengerByEmailAndIsDeletedIsFalse(passengerDto.email())) {
      throw new PassengerAlreadyExistsException(messageSource.getMessage(
          PassengerExceptionMessages.PASSENGER_EMAIL_ALREADY_EXISTS_MESSAGE,
          new Object[] {passengerDto.email()},
          LocaleContextHolder.getLocale()
      ));
    }

    if (passengerRepository.existsPassengerByPhoneNumberAndIsDeletedIsFalse(
        passengerDto.phoneNumber())) {
      throw new PassengerAlreadyExistsException(messageSource.getMessage(
          PassengerExceptionMessages.PASSENGER_PHONE_NUMBER_ALREADY_EXISTS_MESSAGE,
          new Object[] {passengerDto.phoneNumber()},
          LocaleContextHolder.getLocale()
      ));
    }
  }

  public Passenger findPassengerByIdWithChecks(Long id) {
    return passengerRepository.findPassengerByIdAndIsDeletedIsFalse(id)
        .orElseThrow(() -> new PassengerNotFoundException(messageSource.getMessage(
            PassengerExceptionMessages.PASSENGER_NOT_FOUND_MESSAGE,
            new Object[] {id},
            LocaleContextHolder.getLocale()
        )));
  }
}
