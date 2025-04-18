package by.modsen.driverservice.mapper;

import by.modsen.driverservice.constants.ApplicationConstants;
import by.modsen.driverservice.dto.request.DriverRequest;
import by.modsen.driverservice.dto.response.AverageRatingResponse;
import by.modsen.driverservice.dto.response.DriverResponse;
import by.modsen.driverservice.model.Car;
import by.modsen.driverservice.model.Driver;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.keycloak.representations.idm.UserRepresentation;
import org.mapstruct.BeanMapping;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(
    componentModel = "spring",
    injectionStrategy = InjectionStrategy.CONSTRUCTOR
)
public interface DriverMapper {

    @Mapping(target = "carIds", source = "cars", qualifiedByName = "mapCarsToCarIds")
    DriverResponse toResponse(Driver driver);

    @Named("mapCarsToCarIds")
    default List<Long> mapCarsToCarIds(Set<Car> cars) {
        return cars.stream()
            .map(Car::getId)
            .toList();
    }

    @Mapping(target = "cars", ignore = true)
    @Mapping(target = "id", source = "keycloakId")
    Driver toEntity(DriverRequest driverRequest);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "cars", ignore = true)
    void updateDriverFromDto(DriverRequest driverRequest, @MappingTarget Driver driver);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "cars", ignore = true)
    void updateDriverFromDto(AverageRatingResponse averageRatingResponse, @MappingTarget Driver driver);

    default void updateKeycloakUserFromDto(DriverRequest driverRequest,
                                           UserRepresentation userRepresentation) {
        if (driverRequest.firstName() != null) {
            userRepresentation.setFirstName(driverRequest.firstName());
        }
        if (driverRequest.lastName() != null) {
            userRepresentation.setLastName(driverRequest.lastName());
        }
        if (driverRequest.email() != null) {
            userRepresentation.setEmail(driverRequest.email());
            userRepresentation.setUsername(driverRequest.email());
        }

        Map<String, List<String>> attributes = userRepresentation.getAttributes();
        if (attributes == null) {
            attributes = new HashMap<>();
        }
        if (driverRequest.sex() != null) {
            attributes.put(ApplicationConstants.SEX_ATTRIBUTE, List.of(driverRequest.sex()));
        }
        userRepresentation.setAttributes(attributes);
    }

}
