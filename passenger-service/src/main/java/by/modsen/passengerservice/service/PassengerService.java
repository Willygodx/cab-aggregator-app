package by.modsen.passengerservice.service;

import by.modsen.passengerservice.dto.PageResponseDto;
import by.modsen.passengerservice.dto.PassengerDto;

public interface PassengerService {

  PageResponseDto<PassengerDto> getAllPassengers(Integer offset, Integer limit);

  PassengerDto createPassenger(PassengerDto passengerDto);

  PassengerDto updatePassengerById(PassengerDto passengerDto, Long id);

  void deletePassenger(Long id);

  PassengerDto getPassengerById(Long id);

}
