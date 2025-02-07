package by.modsen.ratingservice.mapper;

import by.modsen.ratingservice.dto.request.RatingRequest;
import by.modsen.ratingservice.dto.response.RatingResponse;
import by.modsen.ratingservice.model.Rating;
import by.modsen.ratingservice.model.enums.RatedBy;
import org.mapstruct.AfterMapping;
import org.mapstruct.BeanMapping;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(
    componentModel = MappingConstants.ComponentModel.SPRING,
    injectionStrategy = InjectionStrategy.CONSTRUCTOR,
    unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface RatingMapper {

    @Mapping(target = "ratedBy", ignore = true)
    Rating toEntity(RatingRequest ratingRequest, RatedBy ratedBy);

    RatingResponse toResponse(Rating rating);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateRatingFromDto(RatingRequest ratingRequest, @MappingTarget Rating rating);

    @AfterMapping
    default void setAdditionalFields(@MappingTarget Rating rating, RatedBy ratedBy) {
        if (rating.getRatedBy() == null) {
            rating.setRatedBy(ratedBy);
        }
    }

}
