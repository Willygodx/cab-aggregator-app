package by.modsen.authservice.service.impl;

import by.modsen.authservice.client.driver.DriverFeignClient;
import by.modsen.authservice.client.passenger.PassengerFeignClient;
import by.modsen.authservice.constants.ApplicationConstants;
import by.modsen.authservice.constants.KeycloakConstants;
import by.modsen.authservice.dto.request.AdminSignIn;
import by.modsen.authservice.dto.request.DriverRequestWithKeycloakId;
import by.modsen.authservice.dto.request.PassengerRequestWithKeycloakId;
import by.modsen.authservice.dto.request.SignUp;
import by.modsen.authservice.dto.request.UserSignIn;
import by.modsen.authservice.dto.response.KeycloakAdminTokenResponse;
import by.modsen.authservice.dto.response.KeycloakUserTokenResponse;
import by.modsen.authservice.exception.keycloak.KeycloakUserException;
import by.modsen.authservice.service.AuthService;
import by.modsen.authservice.service.component.AuthServiceUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.ws.rs.ServiceUnavailableException;
import jakarta.ws.rs.core.Response;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.RolesResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final Keycloak keycloak;
    private final DriverFeignClient driverFeignClient;
    private final PassengerFeignClient passengerFeignClient;
    private final ObjectMapper objectMapper;
    private final AuthServiceUtils authServiceUtils;

    @Override
    public void signUp(SignUp signUp) {
        UserRepresentation keycloakUser = createKeycloakUser(signUp);
        RealmResource realmResource = keycloak.realm(KeycloakConstants.KEYCLOAK_REALM);
        UsersResource usersResource = realmResource.users();
        String adminClientAccessToken = keycloak.tokenManager().getAccessTokenString();

        Response response = Optional.ofNullable(usersResource.create(keycloakUser))
            .orElseThrow(() -> new ServiceUnavailableException(authServiceUtils.getServiceUnavailableMessage()));

        if (response.getStatus() == HttpStatus.CREATED.value()) {
            String keycloakUserId = CreatedResponseUtil.getCreatedId(response);
            try {
                createUserInExternalService(signUp, adminClientAccessToken, keycloakUserId);
            } catch (Exception exception) {
                usersResource.delete(keycloakUserId);
                throw exception;
            }
            assignRoleToUser(realmResource, usersResource, signUp.role(), keycloakUserId);
        } else {
            throw new KeycloakUserException(authServiceUtils.createExceptionDto(response));
        }
    }

    @Override
    @SneakyThrows
    public KeycloakUserTokenResponse userSignIn(UserSignIn userSignIn) {
        HttpEntity<MultiValueMap<String, String>> requestEntity = authServiceUtils.createTokenRequestEntity(
            ApplicationConstants.GRANT_TYPE_PASSWORD,
            userSignIn.email(),
            userSignIn.password(),
            KeycloakConstants.KEYCLOAK_AUTH_CLIENT_ID
        );

        ResponseEntity<String>
            response = authServiceUtils.getResponseWithToken(authServiceUtils.getAuthUrl(), requestEntity);
        authServiceUtils.handleAnyStatusCodeExceptOk(response);

        return objectMapper.readValue(response.getBody(), KeycloakUserTokenResponse.class);
    }

    @Override
    @SneakyThrows
    public KeycloakAdminTokenResponse adminSignIn(AdminSignIn adminSignIn) {
        HttpEntity<MultiValueMap<String, String>> requestEntity = authServiceUtils.createTokenRequestEntity(
            ApplicationConstants.GRANT_TYPE_CLIENT_CREDENTIALS,
            KeycloakConstants.KEYCLOAK_ADMIN_CLIENT_ID,
            KeycloakConstants.KEYCLOAK_ADMIN_CLIENT_SECRET
        );

        ResponseEntity<String> response =
            authServiceUtils.getResponseWithToken(authServiceUtils.getAuthUrl(), requestEntity);
        authServiceUtils.handleAnyStatusCodeExceptOk(response);

        return objectMapper.readValue(response.getBody(), KeycloakAdminTokenResponse.class);
    }

    private UserRepresentation createKeycloakUser(SignUp signUp) {
        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setValue(signUp.password());

        Map<String, List<String>> attributes = new HashMap<>();
        attributes.put(ApplicationConstants.SEX_ATTRIBUTE, List.of(signUp.sex()));

        UserRepresentation keycloakUser = new UserRepresentation();
        keycloakUser.setFirstName(signUp.firstName());
        keycloakUser.setLastName(signUp.lastName());
        keycloakUser.setEmail(signUp.email());
        keycloakUser.setCredentials(List.of(credential));
        keycloakUser.setEnabled(true);
        keycloakUser.setAttributes(attributes);

        return keycloakUser;
    }

    private void createUserInExternalService(SignUp signUp, String adminClientAccessToken, String keycloakUserId) {
        if (Objects.equals(signUp.role(), ApplicationConstants.ROLE_PASSENGER)) {
            passengerFeignClient.createPassenger(
                new PassengerRequestWithKeycloakId(
                    signUp.firstName(),
                    signUp.lastName(),
                    signUp.email(),
                    signUp.phoneNumber(),
                    signUp.sex(),
                    keycloakUserId
                ),
                LocaleContextHolder.getLocale().toLanguageTag(),
                ApplicationConstants.BEARER_PREFIX + adminClientAccessToken
            );
        } else {
            driverFeignClient.createDriver(
                new DriverRequestWithKeycloakId(
                    signUp.firstName(),
                    signUp.lastName(),
                    signUp.email(),
                    signUp.phoneNumber(),
                    signUp.sex(),
                    keycloakUserId
                ),
                LocaleContextHolder.getLocale().toLanguageTag(),
                ApplicationConstants.BEARER_PREFIX + adminClientAccessToken
            );
        }
    }

    private void assignRoleToUser(RealmResource realmResource, UsersResource usersResource, String role,
                                  String userId) {
        RolesResource rolesResource = realmResource.roles();
        RoleRepresentation roleRepresentation = rolesResource.get(role).toRepresentation();
        UserResource userResource = usersResource.get(userId);
        userResource.roles().realmLevel().add(List.of(roleRepresentation));
    }

}