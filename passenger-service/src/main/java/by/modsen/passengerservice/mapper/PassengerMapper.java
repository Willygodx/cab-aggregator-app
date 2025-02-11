package by.modsen.passengerservice.mapper;

import by.modsen.passengerservice.dto.request.PassengerRequest;
import by.modsen.passengerservice.dto.response.PassengerResponse;
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

    PassengerResponse toResponse(Passenger passenger);

    Passenger toEntity(PassengerRequest passengerRequest);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updatePassengerFromDto(PassengerRequest passengerRequest, @MappingTarget Passenger passenger);

}
