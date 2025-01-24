package by.modsen.passengerservice.service;

import by.modsen.passengerservice.dto.request.PassengerRequest;
import by.modsen.passengerservice.dto.response.PageResponse;
import by.modsen.passengerservice.dto.response.PassengerResponse;

public interface PassengerService {

  PageResponse<PassengerResponse> getAllPassengers(Integer offset, Integer limit);

  PassengerResponse createPassenger(PassengerRequest passengerRequest);

  PassengerResponse updatePassengerById(PassengerRequest passengerRequest, Long passengerId);

  void deletePassengerById(Long passengerId);

  PassengerResponse getPassengerById(Long passengerId);

}
