package by.modsen.driverservice.service.impl;

import by.modsen.driverservice.constants.ApplicationConstants;
import by.modsen.driverservice.constants.KeycloakConstants;
import by.modsen.driverservice.dto.request.DriverRequest;
import by.modsen.driverservice.dto.response.DriverResponse;
import by.modsen.driverservice.dto.response.PageResponse;
import by.modsen.driverservice.mapper.DriverMapper;
import by.modsen.driverservice.mapper.PageResponseMapper;
import by.modsen.driverservice.model.Car;
import by.modsen.driverservice.model.Driver;
import by.modsen.driverservice.repository.CarRepository;
import by.modsen.driverservice.repository.DriverRepository;
import by.modsen.driverservice.service.DriverService;
import by.modsen.driverservice.service.component.validation.CarServiceValidation;
import by.modsen.driverservice.service.component.validation.DriverServiceValidation;
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
public class DriverServiceImpl implements DriverService {

    private final DriverRepository driverRepository;
    private final CarRepository carRepository;
    private final DriverServiceValidation driverServiceValidation;
    private final CarServiceValidation carServiceValidation;
    private final DriverMapper driverMapper;
    private final PageResponseMapper pageResponseMapper;
    private final Keycloak keycloak;

    @Override
    public PageResponse<DriverResponse> getAllDrivers(Integer offset, Integer limit) {
        Page<DriverResponse> driversPageDto = driverRepository
            .findAllByIsDeletedIsFalse(PageRequest.of(offset, limit))
            .map(driverMapper::toResponse);

        return pageResponseMapper.toDto(driversPageDto);
    }

    @Override
    @Transactional
    public DriverResponse createDriver(DriverRequest driverRequest) {
        driverServiceValidation.restoreOption(driverRequest);
        driverServiceValidation.checkAlreadyExists(driverRequest);

        Driver driver = driverMapper.toEntity(driverRequest);

        driverRepository.save(driver);

        return driverMapper.toResponse(driver);
    }

    @Override
    @Transactional
    public DriverResponse updateDriverById(DriverRequest driverRequest, UUID driverId) {
        driverServiceValidation.restoreOption(driverRequest);
        driverServiceValidation.checkAlreadyExists(driverRequest);

        Driver existingDriver = driverServiceValidation.findDriverByIdWithCheck(driverId);
        updateKeycloakUser(String.valueOf(existingDriver.getId()), driverRequest);

        driverMapper.updateDriverFromDto(driverRequest, existingDriver);

        Driver driver = driverRepository.save(existingDriver);

        return driverMapper.toResponse(driver);
    }

    @Override
    @Transactional
    public void deleteDriverById(UUID driverId) {
        Driver driver = driverServiceValidation.findDriverByIdWithCheck(driverId);
        deleteKeycloakUser(String.valueOf(driver.getId()));

        driver.setIsDeleted(true);

        driver.getCars().clear();

        driverRepository.save(driver);
    }

    @Override
    public DriverResponse getDriverById(UUID driverId) {
        Driver driver = driverServiceValidation.findDriverByIdWithCheck(driverId);

        return driverMapper.toResponse(driver);
    }

    @Override
    @Transactional
    public void addCarToDriver(UUID driverId, Long carId) {
        Driver driver = driverServiceValidation.findDriverByIdWithCheck(driverId);
        Car car = carServiceValidation.findCarByIdWithCheck(carId);

        driver.getCars().add(car);
        car.getDrivers().add(driver);

        driverRepository.save(driver);
        carRepository.save(car);
    }

    private void updateKeycloakUser(String keycloakUserId, DriverRequest driverRequest) {
        RealmResource realmResource = keycloak.realm(KeycloakConstants.KEYCLOAK_REALM);
        UserResource userResource = realmResource.users().get(keycloakUserId);

        UserRepresentation userRepresentation = userResource.toRepresentation();

        driverMapper.updateKeycloakUserFromDto(driverRequest, userRepresentation);

        userResource.update(userRepresentation);
    }

    private void deleteKeycloakUser(String keycloakUserId) {
        RealmResource realmResource = keycloak.realm(KeycloakConstants.KEYCLOAK_REALM);
        Response response = realmResource.users().delete(keycloakUserId);

        if (response.getStatus() != HttpStatus.NO_CONTENT.value()) {
            throw new ServiceUnavailableException(ApplicationConstants.DELETE_KEYCLOAK_USER_FAIL_MESSAGE);
        }
    }

}
