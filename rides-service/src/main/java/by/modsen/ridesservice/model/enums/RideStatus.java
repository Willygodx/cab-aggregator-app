package by.modsen.ridesservice.model.enums;

import java.util.Arrays;
import java.util.Optional;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RideStatus {

    CREATED(0) {
        @Override
        public boolean canTransitionTo(RideStatus newStatus) {
            return newStatus == ACCEPTED || newStatus == DECLINED;
        }
    },
    ACCEPTED(100) {
        @Override
        public boolean canTransitionTo(RideStatus newStatus) {
            return newStatus == ON_THE_WAY_TO_PASSENGER || newStatus == DECLINED;
        }
    },
    ON_THE_WAY_TO_PASSENGER(200) {
        @Override
        public boolean canTransitionTo(RideStatus newStatus) {
            return newStatus == ON_THE_WAY_TO_DESTINATION || newStatus == DECLINED;
        }
    },
    ON_THE_WAY_TO_DESTINATION(300) {
        @Override
        public boolean canTransitionTo(RideStatus newStatus) {
            return newStatus == FINISHED || newStatus == DECLINED;
        }
    },
    FINISHED(400) {
        @Override
        public boolean canTransitionTo(RideStatus newStatus) {
            return false;
        }
    },
    DECLINED(500) {
        @Override
        public boolean canTransitionTo(RideStatus newStatus) {
            return false;
        }
    };

    public abstract boolean canTransitionTo(RideStatus newStatus);

    private final int rideStatusCode;

    public static Optional<RideStatus> fromCode(int rideStatusCode) {
        return Arrays.stream(RideStatus.values())
            .filter(rideStatus -> rideStatus.getRideStatusCode() == rideStatusCode)
            .findAny();
    }

}
