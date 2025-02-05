package by.modsen.ridesservice.model.enums;

import java.util.Arrays;
import java.util.Optional;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RideStatus {

    CREATED(0),
    ACCEPTED(1),
    ON_THE_WAY_TO_PASSENGER(2),
    ON_THE_WAY_TO_DESTINATION(3),
    FINISHED(4),
    DECLINED(5);

    private final int rideStatusCode;

    public static Optional<RideStatus> fromCode(int rideStatusCode) {
        return Arrays.stream(RideStatus.values())
            .filter(rideStatus -> rideStatus.getRideStatusCode() == rideStatusCode)
            .findAny();
    }

}
