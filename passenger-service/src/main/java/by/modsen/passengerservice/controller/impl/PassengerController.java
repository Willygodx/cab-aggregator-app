package by.modsen.passengerservice.controller.impl;

import by.modsen.passengerservice.controller.PassengerOperations;
import by.modsen.passengerservice.dto.PageResponseDto;
import by.modsen.passengerservice.dto.PassengerDto;
import by.modsen.passengerservice.service.impl.PassengerServiceImpl;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
@RequestMapping("/passengers")
public class PassengerController implements PassengerOperations {
  private final PassengerServiceImpl passengerService;

  @GetMapping
  public PageResponseDto<PassengerDto> getAllPassengers(@RequestParam(defaultValue = "0") @Min(0) Integer offset,
                                                        @RequestParam(defaultValue = "10") @Min(1) @Max(100) Integer limit) {
    return passengerService.getAllPassengers(offset, limit);
  }

  @GetMapping("/{passengerId}")
  public PassengerDto getPassengerById(@PathVariable Long passengerId) {
    return passengerService.getPassengerById(passengerId);
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public PassengerDto createPassenger(@RequestBody @Valid PassengerDto passengerDto) {
    return passengerService.createPassenger(passengerDto);
  }

  @PutMapping("/{passengerId}")
  public PassengerDto updatePassengerById(@PathVariable Long passengerId,
                                          @RequestBody @Valid PassengerDto passengerDto) {
    return passengerService.updatePassengerById(passengerDto, passengerId);
  }

  @DeleteMapping("/{passengerId}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deletePassengerById(@PathVariable Long passengerId) {
    passengerService.deletePassenger(passengerId);
  }
}
