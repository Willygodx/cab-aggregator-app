package by.modsen.passengerservice.mapper;

import by.modsen.passengerservice.dto.PassengerDto;
import by.modsen.passengerservice.model.Passenger;
import org.mapstruct.BeanMapping;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(
    componentModel = "spring",
    injectionStrategy = InjectionStrategy.CONSTRUCTOR
)
public interface PassengerMapper {

  PassengerDto toDto(Passenger passenger);

  Passenger toEntity(PassengerDto passengerDto);

  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  void updatePassengerFromDto(PassengerDto passengerDto, @MappingTarget Passenger passenger);
}
