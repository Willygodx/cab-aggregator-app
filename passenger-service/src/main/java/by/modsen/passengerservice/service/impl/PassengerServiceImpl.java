package by.modsen.passengerservice.service.impl;

import by.modsen.passengerservice.dto.response.PageResponseDto;
import by.modsen.passengerservice.dto.request.PassengerRequestDto;
import by.modsen.passengerservice.dto.response.PassengerResponseDto;
import by.modsen.passengerservice.mapper.PageResponseMapper;
import by.modsen.passengerservice.mapper.PassengerMapper;
import by.modsen.passengerservice.model.Passenger;
import by.modsen.passengerservice.repository.PassengerRepository;
import by.modsen.passengerservice.service.PassengerService;
import by.modsen.passengerservice.service.component.validation.PassengerServiceValidation;
import by.modsen.passengerservice.service.component.validation.impl.PassengerServiceValidationImpl;
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
  public PageResponseDto<PassengerResponseDto> getAllPassengers(Integer offset, Integer limit) {
    Page<PassengerResponseDto> passengersPageDto = passengerRepository
        .findAllByIsDeletedIsFalse(PageRequest.of(offset, limit))
        .map(passengerMapper::toResponseDto);

    return pageResponseMapper.toDto(passengersPageDto);
  }

  @Override
  @Transactional
  public PassengerResponseDto createPassenger(PassengerRequestDto passengerDto) {
    passengerServiceValidation.restoreOption(passengerDto);
    passengerServiceValidation.checkAlreadyExists(passengerDto);

    Passenger passenger = passengerMapper.toEntity(passengerDto);
    passenger.setIsDeleted(false);

    passenger = passengerRepository.save(passenger);

    return passengerMapper.toResponseDto(passenger);
  }

  @Override
  @Transactional
  public PassengerResponseDto updatePassengerById(PassengerRequestDto passengerDto, Long passengerId) {
    passengerServiceValidation.restoreOption(passengerDto);
    passengerServiceValidation.checkAlreadyExists(passengerDto);

    Passenger existingPassenger = passengerServiceValidation.findPassengerByIdWithChecks(passengerId);

    passengerMapper.updatePassengerFromDto(passengerDto, existingPassenger);
    existingPassenger.setIsDeleted(false);

    Passenger passenger = passengerRepository.save(existingPassenger);

    return passengerMapper.toResponseDto(passenger);
  }

  @Override
  @Transactional
  public void deletePassengerById(Long passengerId) {
    Passenger passenger = passengerServiceValidation.findPassengerByIdWithChecks(passengerId);
    passenger.setIsDeleted(true);

    passengerRepository.save(passenger);
  }

  @Override
  public PassengerResponseDto getPassengerById(Long passengerId) {
    Passenger passenger = passengerServiceValidation.findPassengerByIdWithChecks(passengerId);

    return passengerMapper.toResponseDto(passenger);
  }

}
