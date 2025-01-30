package by.modsen.ridesservice.mapper;

import by.modsen.ridesservice.dto.request.RideRequest;
import by.modsen.ridesservice.dto.request.RideStatusRequest;
import by.modsen.ridesservice.dto.response.RideResponse;
import by.modsen.ridesservice.model.Ride;
import org.mapstruct.BeanMapping;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(
    componentModel = "spring",
    injectionStrategy = InjectionStrategy.CONSTRUCTOR,
    unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface RideMapper {

  Ride toEntity(RideRequest rideRequest);

  RideResponse toResponseDto(Ride ride);

  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  void updateRideFromDto(RideRequest rideRequest, @MappingTarget Ride ride);

  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  void updateRideFromDto(RideStatusRequest ridesStatus, @MappingTarget Ride ride);

}
