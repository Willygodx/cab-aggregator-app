package by.modsen.passengerservice.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ApplicationExceptionMessages {

    public static final String INTERNAL_SERVER_ERROR_MESSAGE = "Server error occurred!";
    public static final String DEFAULT_ACCESS_DENIED_MESSAGE = "access.denied.message";
    public static final String DELETE_KEYCLOAK_USER_FAIL_MESSAGE = "Failed to delete user from Keycloak";

}
