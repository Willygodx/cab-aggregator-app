package by.modsen.passengerservice.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ApplicationConstants {

    public static final String PHONE_REGEX =
        "^\\+375(25\\d{3}\\d{2}\\d{2}|(29[1-9]\\d{2}\\d{2}\\d{2})|(33\\d{3}\\d{2}\\d{2})|(44\\d{3}\\d{2}\\d{2}))$";

}
