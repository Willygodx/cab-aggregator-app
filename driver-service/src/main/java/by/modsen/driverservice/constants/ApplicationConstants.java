package by.modsen.driverservice.constants;

import java.util.List;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ApplicationConstants {

    public static final String CAR_NUMBER_REGEXP = "^[A-Z]{3}[0-9]{3}$";
    public static final String PHONE_REGEX =
        "^\\+375(25\\d{3}\\d{2}\\d{2}|(29[1-9]\\d{2}\\d{2}\\d{2})|(33\\d{3}\\d{2}\\d{2})|(44\\d{3}\\d{2}\\d{2}))$";
    public static final String INTERNAL_SERVER_ERROR_MESSAGE = "Internal server error occurred!";
    public static final String REQUEST_LOGGING_MESSAGE =
        "Request. Method: {}, URI: {}, Args -> {}";
    public static final String RESPONSE_LOGGING_MESSAGE =
        "Response. Method: {}, URI: {}, Args -> {} Time Taken: {} ms";
    public static final String ERROR_SERIALIZING_JSON_MESSAGE = "Error serializing object to JSON";
    public static final String ROLE_ADMIN_VALUE = "ROLE_ADMIN";
    public static final String SUB_CLAIM = "sub";
    public static final String CLIENT_ID_CLAIM = "azp";
    public static final String ADMIN_CLIENT_ID = "admin-cli";
    public static final String REALM_ACCESS_CLAIM = "realm_access";
    public static final String ROLES_CLAIM = "roles";
    public static final String ROLE_PREFIX = "ROLE_";
    public static final String ADMIN_ROLE = "ROLE_ADMIN";
    public static final List<String> PUBLIC_ENDPOINTS = List.of("/actuator/**");
    public static final String SEX_ATTRIBUTE = "sex";
    public static final String DELETE_KEYCLOAK_USER_FAIL_MESSAGE = "Failed to delete user from Keycloak";

}
