package by.modsen.passengerservice.service.impl;

import by.modsen.passengerservice.dto.PageResponseDto;
import by.modsen.passengerservice.dto.PassengerDto;
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
  public PageResponseDto<PassengerDto> getAllPassengers(Integer offset, Integer limit) {
    Page<PassengerDto> passengersPageDto = passengerRepository
        .findAllByIsDeletedIsFalse(PageRequest.of(offset, limit))
        .map(passengerMapper::toDto);
    return pageResponseMapper.toDto(passengersPageDto);
  }

  @Override
  public PassengerDto createPassenger(PassengerDto passengerDto) {
    passengerServiceValidation.restoreOption(passengerDto);
    passengerServiceValidation.checkAlreadyExists(passengerDto);

    Passenger passenger = passengerMapper.toEntity(passengerDto);
    passenger.setIsDeleted(false);
    passengerRepository.save(passenger);

    return passengerMapper.toDto(passenger);
  }

  @Override
  @Transactional
  public PassengerDto updatePassengerById(PassengerDto passengerDto, Long id) {
    passengerServiceValidation.restoreOption(passengerDto);
    passengerServiceValidation.checkAlreadyExists(passengerDto);

    Passenger existingPassenger = passengerServiceValidation.findPassengerByIdWithChecks(id);

    passengerMapper.updatePassengerFromDto(passengerDto, existingPassenger);
    existingPassenger.setIsDeleted(false);

    return passengerMapper.toDto(passengerRepository.save(existingPassenger));
  }

  @Override
  @Transactional
  public void deletePassenger(Long id) {
    Passenger passenger = passengerServiceValidation.findPassengerByIdWithChecks(id);
    passenger.setIsDeleted(true);

    passengerRepository.save(passenger);
  }

  @Override
  @Transactional
  public PassengerDto getPassengerById(Long id) {
    return passengerMapper.toDto(passengerServiceValidation.findPassengerByIdWithChecks(id));
  }
}
