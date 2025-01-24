package by.modsen.driverservice.mapper;

import by.modsen.driverservice.dto.request.DriverRequest;
import by.modsen.driverservice.dto.response.DriverResponse;
import by.modsen.driverservice.model.Driver;
import org.mapstruct.BeanMapping;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(
    componentModel = "spring",
    injectionStrategy = InjectionStrategy.CONSTRUCTOR
)
public interface DriverMapper {

  DriverResponse toResponse(Driver driver);

  @Mapping(target = "cars", ignore = true)
  Driver toEntity(DriverRequest driverRequest);

  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  @Mapping(target = "cars", ignore = true)
  void updateDriverFromDto(DriverRequest driverRequest, @MappingTarget Driver driver);

}
