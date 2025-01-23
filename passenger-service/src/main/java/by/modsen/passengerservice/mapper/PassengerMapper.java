package by.modsen.passengerservice.mapper;

import by.modsen.passengerservice.dto.request.PassengerRequestDto;
import by.modsen.passengerservice.dto.response.PassengerResponseDto;
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

  PassengerResponseDto toResponseDto(Passenger passenger);

  Passenger toEntity(PassengerRequestDto passengerDto);

  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  void updatePassengerFromDto(PassengerRequestDto passengerDto, @MappingTarget Passenger passenger);

}
