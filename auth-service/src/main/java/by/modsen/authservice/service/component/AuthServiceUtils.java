package by.modsen.authservice.service.component;

import by.modsen.authservice.dto.ExceptionDto;
import jakarta.ws.rs.core.Response;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;

public interface AuthServiceUtils {

    HttpEntity<MultiValueMap<String, String>> createTokenRequestEntity(String grantType, String... params);

    ResponseEntity<String> getResponseWithToken(String authUrl,
                                                HttpEntity<MultiValueMap<String, String>> requestEntity);

    void handleAnyStatusCodeExceptOk(ResponseEntity<String> response);

    ExceptionDto createExceptionDto(Response response);

    ExceptionDto createExceptionDto(ResponseEntity<String> response);

    ExceptionDto createExceptionDto(HttpClientErrorException exception);

    String getAuthUrl();

    String getServiceUnavailableMessage();

    String readResponseBody(Response response);

}
