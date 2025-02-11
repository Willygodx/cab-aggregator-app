package by.modsen.passengerservice.service.impl;

import by.modsen.passengerservice.dto.request.PassengerRequest;
import by.modsen.passengerservice.dto.response.PageResponse;
import by.modsen.passengerservice.dto.response.PassengerResponse;
import by.modsen.passengerservice.mapper.PageResponseMapper;
import by.modsen.passengerservice.mapper.PassengerMapper;
import by.modsen.passengerservice.model.Passenger;
import by.modsen.passengerservice.repository.PassengerRepository;
import by.modsen.passengerservice.service.PassengerService;
import by.modsen.passengerservice.service.component.validation.PassengerServiceValidation;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PassengerServiceImpl implements PassengerService {

    private final PassengerRepository passengerRepository;
    private final PassengerMapper passengerMapper;
    private final PageResponseMapper pageResponseMapper;
    private final PassengerServiceValidation passengerServiceValidation;

    @Override
    public PageResponse<PassengerResponse> getAllPassengers(Integer offset, Integer limit) {
        Page<PassengerResponse> passengersPageDto = passengerRepository
            .findAllByIsDeletedIsFalse(PageRequest.of(offset, limit))
            .map(passengerMapper::toResponse);

        return pageResponseMapper.toDto(passengersPageDto);
    }

    @Override
    @Transactional
    public PassengerResponse createPassenger(PassengerRequest passengerRequest) {
        passengerServiceValidation.restoreOption(passengerRequest);
        passengerServiceValidation.checkAlreadyExists(passengerRequest);

        Passenger passenger = passengerMapper.toEntity(passengerRequest);
        passenger.setIsDeleted(false);

        passenger = passengerRepository.save(passenger);

        return passengerMapper.toResponse(passenger);
    }

    @Override
    @Transactional
    public PassengerResponse updatePassengerById(PassengerRequest passengerRequest, Long passengerId) {
        passengerServiceValidation.restoreOption(passengerRequest);
        passengerServiceValidation.checkAlreadyExists(passengerRequest);

        Passenger existingPassenger = passengerServiceValidation.findPassengerByIdWithChecks(passengerId);

        passengerMapper.updatePassengerFromDto(passengerRequest, existingPassenger);
        existingPassenger.setIsDeleted(false);

        Passenger passenger = passengerRepository.save(existingPassenger);

        return passengerMapper.toResponse(passenger);
    }

    @Override
    @Transactional
    public void deletePassengerById(Long passengerId) {
        Passenger passenger = passengerServiceValidation.findPassengerByIdWithChecks(passengerId);
        passenger.setIsDeleted(true);

        passengerRepository.save(passenger);
    }

    @Override
    public PassengerResponse getPassengerById(Long passengerId) {
        Passenger passenger = passengerServiceValidation.findPassengerByIdWithChecks(passengerId);

        return passengerMapper.toResponse(passenger);
    }

}
