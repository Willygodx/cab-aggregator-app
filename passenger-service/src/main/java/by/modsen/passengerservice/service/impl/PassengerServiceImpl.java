package by.modsen.passengerservice.service.impl;

import by.modsen.passengerservice.constants.ApplicationExceptionMessages;
import by.modsen.passengerservice.constants.KeycloakConstants;
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
import jakarta.ws.rs.ServiceUnavailableException;
import jakarta.ws.rs.core.Response;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PassengerServiceImpl implements PassengerService {

    private final PassengerRepository passengerRepository;
    private final PassengerMapper passengerMapper;
    private final PageResponseMapper pageResponseMapper;
    private final PassengerServiceValidation passengerServiceValidation;
    private final Keycloak keycloak;

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
    public PassengerResponse updatePassengerById(PassengerRequest passengerRequest, UUID passengerId) {
        passengerServiceValidation.restoreOption(passengerRequest);
        passengerServiceValidation.checkAlreadyExists(passengerRequest);

        Passenger existingPassenger = passengerServiceValidation.findPassengerByIdWithChecks(passengerId);
        updateKeycloakUser(String.valueOf(existingPassenger.getId()), passengerRequest);

        passengerMapper.updatePassengerFromDto(passengerRequest, existingPassenger);
        existingPassenger.setIsDeleted(false);

        Passenger passenger = passengerRepository.save(existingPassenger);

        return passengerMapper.toResponse(passenger);
    }

    @Override
    @Transactional
    public void deletePassengerById(UUID passengerId) {
        Passenger passenger = passengerServiceValidation.findPassengerByIdWithChecks(passengerId);
        deleteKeycloakUser(String.valueOf(passenger.getId()));

        passenger.setIsDeleted(true);
        passengerRepository.save(passenger);
    }

    @Override
    public PassengerResponse getPassengerById(UUID passengerId) {
        Passenger passenger = passengerServiceValidation.findPassengerByIdWithChecks(passengerId);

        return passengerMapper.toResponse(passenger);
    }

    private void updateKeycloakUser(String keycloakUserId, PassengerRequest passengerRequest) {
        RealmResource realmResource = keycloak.realm(KeycloakConstants.KEYCLOAK_REALM);
        UserResource userResource = realmResource.users().get(keycloakUserId);

        UserRepresentation userRepresentation = userResource.toRepresentation();

        passengerMapper.updateKeycloakUserFromDto(passengerRequest, userRepresentation);

        userResource.update(userRepresentation);
    }

    private void deleteKeycloakUser(String keycloakUserId) {
        RealmResource realmResource = keycloak.realm(KeycloakConstants.KEYCLOAK_REALM);
        Response response = realmResource.users().delete(keycloakUserId);

        if (response.getStatus() != HttpStatus.NO_CONTENT.value()) {
            throw new ServiceUnavailableException(ApplicationExceptionMessages.DELETE_KEYCLOAK_USER_FAIL_MESSAGE);
        }
    }

}
