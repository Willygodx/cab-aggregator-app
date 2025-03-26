package by.modsen.passengerservice.controller.impl;

import by.modsen.passengerservice.aspect.ValidateAccess;
import by.modsen.passengerservice.controller.PassengerOperations;
import by.modsen.passengerservice.dto.request.PassengerRequest;
import by.modsen.passengerservice.dto.response.PageResponse;
import by.modsen.passengerservice.dto.response.PassengerResponse;
import by.modsen.passengerservice.service.PassengerService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/passengers")
public class PassengerController implements PassengerOperations {

    private final PassengerService passengerService;

    @GetMapping
    public PageResponse<PassengerResponse> getAllPassengers(@RequestParam(defaultValue = "0") @Min(0) Integer offset,
                                                            @RequestParam(defaultValue = "10")
                                                            @Min(1) @Max(100) Integer limit) {
        return passengerService.getAllPassengers(offset, limit);
    }

    @GetMapping("/{passengerId}")
    public PassengerResponse getPassengerById(@PathVariable UUID passengerId) {
        return passengerService.getPassengerById(passengerId);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    public PassengerResponse createPassenger(@RequestBody @Valid PassengerRequest passengerRequest) {
        return passengerService.createPassenger(passengerRequest);
    }

    @PutMapping("/{passengerId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('PASSENGER')")
    @ValidateAccess
    public PassengerResponse updatePassengerById(@PathVariable UUID passengerId,
                                                 @RequestBody @Valid PassengerRequest passengerRequest,
                                                 JwtAuthenticationToken jwt) {
        return passengerService.updatePassengerById(passengerRequest, passengerId);
    }

    @DeleteMapping("/{passengerId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('PASSENGER')")
    @ValidateAccess
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePassengerById(@PathVariable UUID passengerId,
                                    JwtAuthenticationToken jwt) {
        passengerService.deletePassengerById(passengerId);
    }

}
