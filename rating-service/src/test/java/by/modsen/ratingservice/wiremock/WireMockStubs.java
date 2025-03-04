package by.modsen.ratingservice.wiremock;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;

import by.modsen.ratingservice.client.driver.DriverResponse;
import by.modsen.ratingservice.client.passenger.PassengerResponse;
import by.modsen.ratingservice.client.ride.RideResponse;
import by.modsen.ratingservice.constants.IntegrationTestDataConstants;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import org.apache.http.HttpHeaders;
import org.springframework.http.MediaType;

public class WireMockStubs {

    public static void getDriverResponseStub(WireMockServer wireMockServer,
                                             ObjectMapper objectMapper,
                                             DriverResponse response) throws Exception {
        wireMockServer.stubFor(get(urlEqualTo(IntegrationTestDataConstants.DRIVER_FROM_ANOTHER_SERVICE_ENDPOINT))
            .willReturn(aResponse()
                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .withBody(objectMapper.writeValueAsString(response))));
    }

    public static void getPassengerResponseStub(WireMockServer wireMockServer,
                                                ObjectMapper objectMapper,
                                                PassengerResponse response) throws Exception {
        wireMockServer.stubFor(get(urlEqualTo(IntegrationTestDataConstants.PASSENGER_FROM_ANOTHER_SERVICE_ENDPOINT))
            .willReturn(aResponse()
                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .withBody(objectMapper.writeValueAsString(response))));
    }

    public static void getRideResponseStub(WireMockServer wireMockServer,
                                           ObjectMapper objectMapper,
                                           RideResponse response) throws Exception {
        wireMockServer.stubFor(get(urlEqualTo(IntegrationTestDataConstants.RIDE_FROM_ANOTHER_SERVICE_ENDPOINT))
            .willReturn(aResponse()
                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .withBody(objectMapper.writeValueAsString(response))));
    }

}
