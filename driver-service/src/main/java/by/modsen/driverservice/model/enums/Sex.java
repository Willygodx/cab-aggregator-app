package by.modsen.driverservice.model.enums;

import java.util.Arrays;
import java.util.Optional;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Sex {

    MALE(0),
    FEMALE(1),
    NONE(2),
    OTHER(3);

    private final int sexCode;

    public static Optional<Sex> fromCode(int sexCode) {
        return Arrays.stream(Sex.values())
            .filter(sex -> sex.getSexCode() == sexCode)
            .findAny();
    }

}
