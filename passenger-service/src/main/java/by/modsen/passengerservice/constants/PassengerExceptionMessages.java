package by.modsen.passengerservice.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class PassengerExceptionMessages {

  public static final String PASSENGER_NOT_FOUND_MESSAGE = "passenger.not.found.message";
  public static final String PASSENGER_EMAIL_ALREADY_EXISTS_MESSAGE = "passenger.email.already.exists.message";
  public static final String PASSENGER_PHONE_NUMBER_ALREADY_EXISTS_MESSAGE = "passenger.phone.already.exists.message";
  public static final String PASSENGER_RESTORE_PHONE_OPTION_MESSAGE = "passenger.restore.phone.option";
  public static final String PASSENGER_RESTORE_EMAIL_OPTION_MESSAGE = "passenger.restore.email.option";

}
