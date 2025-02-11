package by.modsen.driverservice.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class DriverExceptionMessageKeys {

    public static final String DRIVER_NOT_FOUND_MESSAGE_KEY = "driver.not.found.message";
    public static final String DRIVER_ALREADY_EXISTS_BY_EMAIL_MESSAGE_KEY = "driver.email.already.exists.message";
    public static final String DRIVER_ALREADY_EXISTS_BY_PHONE_KEY = "driver.phone_number.already.exists.message";
    public static final String DRIVER_RESTORE_EMAIL_OPTION_MESSAGE_KEY = "driver.restore.email.option";
    public static final String DRIVER_RESTORE_PHONE_OPTION_MESSAGE_KEY = "driver.restore.phone_number.option";

}
