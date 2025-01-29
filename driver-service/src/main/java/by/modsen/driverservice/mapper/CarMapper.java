package by.modsen.driverservice.mapper;

import by.modsen.driverservice.dto.request.CarRequest;
import by.modsen.driverservice.dto.response.CarResponse;
import by.modsen.driverservice.model.Car;
import by.modsen.driverservice.model.Driver;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
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
public interface CarMapper {

  @Mapping(target = "driverIds", source = "drivers", qualifiedByName = "mapDriversToDriverIds")
  CarResponse toResponse(Car car);

  @Named("mapDriversToDriverIds")
  default List<Long> mapDriversToDriverIds(Set<Driver> drivers) {
    return drivers.stream()
        .map(Driver::getId)
        .toList();
  }

  @Mapping(target = "drivers", ignore = true)
  Car toEntity(CarRequest carRequest);

  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  @Mapping(target = "drivers", ignore = true)
  void updateCarFromDto(CarRequest carRequest, @MappingTarget Car car);

}
