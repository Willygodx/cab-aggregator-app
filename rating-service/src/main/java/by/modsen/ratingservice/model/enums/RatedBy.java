package by.modsen.ratingservice.model.enums;

import by.modsen.ratingservice.constants.ApplicationExceptionMessageKeys;
import by.modsen.ratingservice.exception.converter.RatedByConversionException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RatedBy {

    PASSENGER(0),
    DRIVER(1);

    private final int ratedByCode;

    public static RatedBy fromCode(int code) {
        for (RatedBy value : RatedBy.values()) {
            if (value.ratedByCode == code) {
                return value;
            }
        }
        throw new RatedByConversionException(ApplicationExceptionMessageKeys.INTERNAL_SERVER_ERROR_MESSAGE);
    }

}
