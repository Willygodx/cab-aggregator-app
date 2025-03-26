package by.modsen.authservice.service.component.impl;

import by.modsen.authservice.constants.ApplicationConstants;
import by.modsen.authservice.constants.KeycloakConstants;
import by.modsen.authservice.dto.ExceptionDto;
import by.modsen.authservice.exception.keycloak.KeycloakException;
import by.modsen.authservice.exception.keycloak.KeycloakUserException;
import by.modsen.authservice.service.component.AuthServiceUtils;
import jakarta.ws.rs.core.Response;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class AuthServiceUtilsImpl implements AuthServiceUtils {

    private final RestTemplate restTemplate;
    private final MessageSource messageSource;

    public HttpEntity<MultiValueMap<String, String>> createTokenRequestEntity(String grantType, String... params) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.valueOf(ApplicationConstants.APPLICATION_FORM_URLENCODED));

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add(ApplicationConstants.GRANT_TYPE, grantType);

        if (grantType.equals(ApplicationConstants.GRANT_TYPE_PASSWORD)) {
            body.add(ApplicationConstants.USERNAME, params[0]);
            body.add(ApplicationConstants.PASSWORD, params[1]);
            body.add(ApplicationConstants.CLIENT_ID, params[2]);
        } else {
            body.add(ApplicationConstants.CLIENT_ID, params[0]);
            body.add(ApplicationConstants.CLIENT_SECRET, params[1]);
        }

        return new HttpEntity<>(body, headers);
    }

    public ResponseEntity<String> getResponseWithToken(String authUrl,
                                                       HttpEntity<MultiValueMap<String, String>> requestEntity) {
        try {
            return restTemplate.exchange(authUrl, HttpMethod.POST, requestEntity, String.class);
        } catch (HttpClientErrorException exception) {
            throw new KeycloakUserException(createExceptionDto(exception));
        }
    }

    public void handleAnyStatusCodeExceptOk(ResponseEntity<String> response) {
        if (response.getStatusCode() != HttpStatus.OK) {
            throw new KeycloakException(createExceptionDto(response));
        }
    }

    public ExceptionDto createExceptionDto(Response response) {
        return new ExceptionDto(readResponseBody(response), HttpStatus.valueOf(response.getStatus()),
            LocalDateTime.now());
    }

    public ExceptionDto createExceptionDto(ResponseEntity<String> response) {
        return new ExceptionDto(response.getBody(), HttpStatus.valueOf(response.getStatusCode().value()),
            LocalDateTime.now());
    }

    public ExceptionDto createExceptionDto(HttpClientErrorException exception) {
        return new ExceptionDto(exception.getMessage(), HttpStatus.valueOf(exception.getStatusCode().value()),
            LocalDateTime.now());
    }

    public String getAuthUrl() {
        return KeycloakConstants.KEYCLOAK_ADMIN_CLIENT_SERVER_URL +
            "/realms/" + KeycloakConstants.KEYCLOAK_REALM +
            "/protocol/openid-connect/token";
    }

    public String getServiceUnavailableMessage() {
        return messageSource.getMessage(
            ApplicationConstants.SERVICE_UNAVAILABLE_MESSAGE_KEY,
            new Object[] {},
            LocaleContextHolder.getLocale()
        );
    }

    @SneakyThrows
    public String readResponseBody(Response response) {
        if (Objects.isNull(response)) {
            return ApplicationConstants.EMPTY_STRING;
        }

        try (InputStream inputStream = response.readEntity(InputStream.class);
             InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
             BufferedReader reader = new BufferedReader(inputStreamReader)) {

            StringBuilder builder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }

            return builder.toString()
                .replace(ApplicationConstants.DOUBLE_QUOTE + ApplicationConstants.ERROR_MESSAGE_KEY +
                    ApplicationConstants.DOUBLE_QUOTE + ":", "")
                .replace(ApplicationConstants.DOUBLE_QUOTE, ApplicationConstants.EMPTY_STRING);
        }
    }

}
