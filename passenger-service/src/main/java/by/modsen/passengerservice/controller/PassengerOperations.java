package by.modsen.passengerservice.controller;

import by.modsen.passengerservice.dto.Marker;
import by.modsen.passengerservice.dto.PageResponseDto;
import by.modsen.passengerservice.dto.PassengerDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@Validated
@Tag(name = "Passenger controller",
    description = "This controller contains CRUD operations for passenger service")
public interface PassengerOperations {

  @Operation(description = "Retrieving all passengers (pagination type)")
  PageResponseDto<PassengerDto> getAllPassengers(@RequestParam(defaultValue = "0") @Min(0) Integer offset,
                                                 @RequestParam(defaultValue = "10") @Min(1) @Max(100) Integer limit);

  @Operation(description = "Retrieving a passenger by id")
  PassengerDto getPassengerById(@PathVariable Long passengerId);

  @Operation(description = "Creates a passenger")
  @Validated(Marker.OnCreate.class)
  PassengerDto createPassenger(@RequestBody @Valid PassengerDto passengerDto);

  @Operation(description = "Updates a passenger")
  @Validated(Marker.OnUpdate.class)
  PassengerDto updatePassengerById(@PathVariable Long passengerId,
                                   @RequestBody @Valid PassengerDto passengerDto);

  @Operation(description = "Deletes a passenger")
  void deletePassengerById(@PathVariable Long passengerId);
}
