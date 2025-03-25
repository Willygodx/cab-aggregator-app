package by.modsen.authservice.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class KeycloakConstants {

    public static final String KEYCLOAK_REALM = getEnvOrDefault("KEYCLOAK_REALM", "cab-aggregator");
    public static final String KEYCLOAK_ADMIN_CLIENT_ID =
        getEnvOrDefault("KEYCLOAK_ADMIN_CLIENT_ID", "admin-cli");
    public static final String KEYCLOAK_ADMIN_CLIENT_SECRET =
        getEnvOrDefault("KEYCLOAK_ADMIN_CLIENT_SECRET", "T0v8gzHHe5DLXPWgguXF8FIImSmOaYq4");
    public static final String KEYCLOAK_ADMIN_CLIENT_USERNAME =
        getEnvOrDefault("KEYCLOAK_ADMIN_CLIENT_USERNAME", "admin");
    public static final String KEYCLOAK_ADMIN_CLIENT_PASSWORD =
        getEnvOrDefault("KEYCLOAK_ADMIN_CLIENT_PASSWORD", "password");
    public static final String KEYCLOAK_ADMIN_CLIENT_SERVER_URL =
        getEnvOrDefault("KEYCLOAK_ADMIN_CLIENT_SERVER_URL", "http://localhost:8081");
    public static final String KEYCLOAK_AUTH_CLIENT_ID =
        getEnvOrDefault("KEYCLOAK_AUTH_CLIENT_ID", "cab-aggregator-client");

    private static String getEnvOrDefault(String envName, String defaultValue) {
        String value = System.getenv(envName);
        return value != null ? value : defaultValue;
    }

}
