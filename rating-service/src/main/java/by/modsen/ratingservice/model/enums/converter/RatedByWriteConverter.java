package by.modsen.ratingservice.model.enums.converter;

import by.modsen.ratingservice.model.enums.RatedBy;
import lombok.NonNull;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;

@WritingConverter
public class RatedByWriteConverter implements Converter<RatedBy, Integer> {

    @Override
    public Integer convert(@NonNull RatedBy source) {
        return source.getRatedByCode();
    }

}
