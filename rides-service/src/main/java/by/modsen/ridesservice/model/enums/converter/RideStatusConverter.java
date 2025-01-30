package by.modsen.ridesservice.model.enums.converter;

import by.modsen.ridesservice.constants.ApplicationExceptionMessageKeys;
import by.modsen.ridesservice.exception.converter.RideStatusConversionException;
import by.modsen.ridesservice.model.enums.RideStatus;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

@Converter
@RequiredArgsConstructor
public class RideStatusConverter implements AttributeConverter<RideStatus, Integer> {

  private final MessageSource messageSource;

  @Override
  public Integer convertToDatabaseColumn(RideStatus rideStatus) {
    if (rideStatus == null) {
      throw new RideStatusConversionException(messageSource.getMessage(
          ApplicationExceptionMessageKeys.SERVER_ERROR_MESSAGE,
          new Object[] {},
          LocaleContextHolder.getLocale()
      ));
    }

    return rideStatus.getRideStatusCode();
  }

  @Override
  public RideStatus convertToEntityAttribute(Integer code) {
    return RideStatus.fromCode(code)
        .orElseThrow(() -> new RideStatusConversionException(messageSource.getMessage(
            ApplicationExceptionMessageKeys.SERVER_ERROR_MESSAGE,
            new Object[] {},
            LocaleContextHolder.getLocale()
        )));
  }

}
