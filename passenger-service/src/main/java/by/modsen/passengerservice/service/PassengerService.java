package by.modsen.passengerservice.service;

import by.modsen.passengerservice.dto.response.PageResponseDto;
import by.modsen.passengerservice.dto.request.PassengerRequestDto;
import by.modsen.passengerservice.dto.response.PassengerResponseDto;

public interface PassengerService {

  PageResponseDto<PassengerResponseDto> getAllPassengers(Integer offset, Integer limit);

  PassengerResponseDto createPassenger(PassengerRequestDto passengerDto);

  PassengerResponseDto updatePassengerById(PassengerRequestDto passengerDto, Long passengerId);

  void deletePassengerById(Long passengerId);

  PassengerResponseDto getPassengerById(Long passengerId);

}
