package by.modsen.driverservice.model.enums.converter;

import by.modsen.driverservice.constants.ApplicationExceptionMessageKeys;
import by.modsen.driverservice.exception.converter.SexConversionException;
import by.modsen.driverservice.model.enums.Sex;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

@Converter
@RequiredArgsConstructor
public class SexEnumConverter implements AttributeConverter<Sex, Integer> {

    private final MessageSource messageSource;

    @Override
    public Integer convertToDatabaseColumn(Sex sex) {
        if (sex == null) {
            throw new SexConversionException(messageSource.getMessage(
                ApplicationExceptionMessageKeys.SERVER_ERROR_MESSAGE,
                new Object[] {},
                LocaleContextHolder.getLocale()
            ));
        }

        return sex.getSexCode();
    }

    @Override
    public Sex convertToEntityAttribute(Integer code) {
        return Sex.fromCode(code)
            .orElseThrow(() -> new SexConversionException(messageSource.getMessage(
                ApplicationExceptionMessageKeys.SERVER_ERROR_MESSAGE,
                new Object[] {},
                LocaleContextHolder.getLocale()
            )));
    }

}
