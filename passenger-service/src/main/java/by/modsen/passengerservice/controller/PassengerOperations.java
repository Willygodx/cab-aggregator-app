package by.modsen.passengerservice.controller;

import by.modsen.passengerservice.dto.Marker;
import by.modsen.passengerservice.dto.request.PassengerRequest;
import by.modsen.passengerservice.dto.response.PageResponse;
import by.modsen.passengerservice.dto.response.PassengerResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.util.UUID;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@Validated
@Tag(name = "Passenger controller",
    description = "This controller contains CRUD operations for passenger service")
public interface PassengerOperations {

    @Operation(description = "Retrieving all passengers (pagination type)")
    PageResponse<PassengerResponse> getAllPassengers(@RequestParam(defaultValue = "0") @Min(0) Integer offset,
                                                     @RequestParam(defaultValue = "10")
                                                     @Min(1) @Max(100) Integer limit);

    @Operation(description = "Retrieving a passenger by id")
    PassengerResponse getPassengerById(@PathVariable UUID passengerId);

    @Operation(description = "Creates a passenger")
    @Validated(Marker.OnCreate.class)
    PassengerResponse createPassenger(@RequestBody @Valid PassengerRequest passengerRequest);

    @Operation(description = "Updates a passenger")
    @Validated(Marker.OnUpdate.class)
    PassengerResponse updatePassengerById(@PathVariable UUID passengerId,
                                          @RequestBody @Valid PassengerRequest passengerRequest,
                                          JwtAuthenticationToken jwt);

    @Operation(description = "Deletes a passenger")
    void deletePassengerById(@PathVariable UUID passengerId,
                             JwtAuthenticationToken jwt);

}
