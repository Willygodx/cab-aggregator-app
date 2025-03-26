package by.modsen.authservice.configuration;

import by.modsen.authservice.constants.KeycloakConstants;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class KeycloakConfig {

    @Bean
    public Keycloak keycloak() {
        return KeycloakBuilder.builder()
            .serverUrl(KeycloakConstants.KEYCLOAK_ADMIN_CLIENT_SERVER_URL)
            .realm(KeycloakConstants.KEYCLOAK_REALM)
            .grantType(OAuth2Constants.CLIENT_CREDENTIALS)
            .clientId(KeycloakConstants.KEYCLOAK_ADMIN_CLIENT_ID)
            .clientSecret(KeycloakConstants.KEYCLOAK_ADMIN_CLIENT_SECRET)
            .username(KeycloakConstants.KEYCLOAK_ADMIN_CLIENT_USERNAME)
            .password(KeycloakConstants.KEYCLOAK_ADMIN_CLIENT_PASSWORD)
            .build();
    }

    @Bean
    RestTemplate restTemplate() {
        return new RestTemplate();
    }

}
