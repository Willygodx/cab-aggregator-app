package by.modsen.ridesservice.mapper;

import by.modsen.ridesservice.dto.request.RideRequest;
import by.modsen.ridesservice.dto.request.RideStatusRequest;
import by.modsen.ridesservice.dto.response.RideResponse;
import by.modsen.ridesservice.model.Ride;
import by.modsen.ridesservice.model.enums.RideStatus;
import by.modsen.ridesservice.service.component.RideServicePriceGenerator;
import java.math.BigDecimal;
import java.time.LocalDateTime;
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
public interface RideMapper {

    @Mapping(target = "rideStatus", ignore = true)
    @Mapping(target = "cost", ignore = true)
    @Mapping(target = "orderDateTime", ignore = true)
    Ride toEntity(RideRequest rideRequest, BigDecimal cost);

    RideResponse toResponse(Ride ride);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateRideFromDto(RideRequest rideRequest, @MappingTarget Ride ride);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateRideFromDto(RideStatusRequest ridesStatus, @MappingTarget Ride ride);

    @AfterMapping
    default void setAdditionalFields(@MappingTarget Ride ride, BigDecimal cost) {
        if (ride.getRideStatus() == null) {
            ride.setRideStatus(RideStatus.CREATED);
        }
        if (ride.getCost() == null) {
            ride.setCost(cost);
        }
        if (ride.getOrderDateTime() == null) {
            ride.setOrderDateTime(LocalDateTime.now());
        }
    }

}
