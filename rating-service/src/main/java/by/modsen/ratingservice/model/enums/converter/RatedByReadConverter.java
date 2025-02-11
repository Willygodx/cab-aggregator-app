package by.modsen.ratingservice.model.enums.converter;

import by.modsen.ratingservice.model.enums.RatedBy;
import lombok.NonNull;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;

@ReadingConverter
public class RatedByReadConverter implements Converter<Integer, RatedBy> {

    @Override
    public RatedBy convert(@NonNull Integer source) {
        return RatedBy.fromCode(source);
    }

}
