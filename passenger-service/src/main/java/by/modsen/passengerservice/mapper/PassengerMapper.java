package by.modsen.passengerservice.mapper;

import by.modsen.passengerservice.dto.request.PassengerRequest;
import by.modsen.passengerservice.dto.response.AverageRatingResponse;
import by.modsen.passengerservice.dto.response.PassengerResponse;
import by.modsen.passengerservice.model.Passenger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.keycloak.representations.idm.UserRepresentation;
import org.mapstruct.BeanMapping;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(
    componentModel = MappingConstants.ComponentModel.SPRING,
    injectionStrategy = InjectionStrategy.CONSTRUCTOR
)
public interface PassengerMapper {

    PassengerResponse toResponse(Passenger passenger);

    @Mapping(target = "id", source = "keycloakId")
    Passenger toEntity(PassengerRequest passengerRequest);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updatePassengerFromDto(PassengerRequest passengerRequest, @MappingTarget Passenger passenger);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updatePassengerFromDto(AverageRatingResponse averageRatingResponse, @MappingTarget Passenger passenger);

    default void updateKeycloakUserFromDto(PassengerRequest passengerRequest,
                                           UserRepresentation userRepresentation) {
        if (passengerRequest.firstName() != null) {
            userRepresentation.setFirstName(passengerRequest.firstName());
        }
        if (passengerRequest.lastName() != null) {
            userRepresentation.setLastName(passengerRequest.lastName());
        }
        if (passengerRequest.email() != null) {
            userRepresentation.setEmail(passengerRequest.email());
            userRepresentation.setUsername(passengerRequest.email());
        }

        Map<String, List<String>> attributes = userRepresentation.getAttributes();
        if (attributes == null) {
            attributes = new HashMap<>();
        }
        userRepresentation.setAttributes(attributes);
    }

}
