package by.modsen.passengerservice.service;

import by.modsen.passengerservice.dto.request.PassengerRequest;
import by.modsen.passengerservice.dto.response.PageResponse;
import by.modsen.passengerservice.dto.response.PassengerResponse;
import java.util.UUID;

public interface PassengerService {

    PageResponse<PassengerResponse> getAllPassengers(Integer offset, Integer limit);

    PassengerResponse createPassenger(PassengerRequest passengerRequest);

    PassengerResponse updatePassengerById(PassengerRequest passengerRequest, UUID passengerId);

    void deletePassengerById(UUID passengerId);

    PassengerResponse getPassengerById(UUID passengerId);

}
