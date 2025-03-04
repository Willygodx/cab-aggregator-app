package by.modsen.ridesservice.controller.impl;

import static by.modsen.ridesservice.constants.IntegrationTestDataConstants.*;
import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

import by.modsen.ridesservice.dto.response.PageResponse;
import by.modsen.ridesservice.dto.response.RideResponse;
import by.modsen.ridesservice.exception.validation.ValidationResponse;
import by.modsen.ridesservice.wiremock.WireMockStubs;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureWireMock(port = 9090)
@Sql(statements = {
    DELETE_RIDES_SQL,
    RESET_RIDE_ID_SEQUENCE_SQL,
    INSERT_RIDE_SQL
}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class RideControllerIntegrationTest {

    @DynamicPropertySource
    private static void disableEureka(DynamicPropertyRegistry registry) {
        registry.add(EUREKA_CLIENT_ENABLED_PROPERTY, () -> BOOLEAN_PROPERTY_VALUE);
    }

    @ServiceConnection
    @Container
    static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>(
        DockerImageName.parse(POSTGRESQL_IMAGE_NAME)
    );

    @LocalServerPort
    private int port;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private WireMockServer wireMockServer;

    @BeforeEach
    void setup() {
        RestAssured.port = this.port;
    }

    @Test
    void getRideById_ReturnsRideDto_DatabaseContainsSuchRideId() {
        Response response = given()
            .when()
            .get(RIDE_BY_ID_ENDPOINT, RIDE_ID)
            .then()
            .contentType(ContentType.JSON)
            .statusCode(HttpStatus.OK.value())
            .extract()
            .response();
        RideResponse actual = response.as(RideResponse.class);
        assertThat(actual.id()).isEqualTo(RIDE_ID);
    }

    @Test
    void getRideById_ReturnsNotFound_DatabaseDoesNotContainSuchRideId() {
        given()
            .when()
            .get(RIDE_BY_ID_ENDPOINT, 999)
            .then()
            .contentType(ContentType.JSON)
            .statusCode(HttpStatus.NOT_FOUND.value());
    }

    @Test
    void createRide_ReturnsCreatedRideDto_AllMandatoryFieldsInRequestBody() throws Exception {
        WireMockStubs.getPassengerResponseStub(wireMockServer, objectMapper, PASSENGER_RESPONSE);
        WireMockStubs.getDriverResponseStub(wireMockServer, objectMapper, DRIVER_RESPONSE);

        Response response = given()
            .contentType(ContentType.JSON)
            .body(RIDE_REQUEST)
            .when()
            .post(RIDE_ENDPOINT)
            .then()
            .statusCode(HttpStatus.CREATED.value())
            .extract()
            .response();
        RideResponse actual = response.as(RideResponse.class);
        assertThat(actual.pickupAddress()).isEqualTo(PICKUP_ADDRESS);
    }

    @Test
    void createRide_ReturnsBadRequest_MissingMandatoryFields() {
        Response response = given()
            .contentType(ContentType.JSON)
            .body(INVALID_RIDE_REQUEST)
            .when()
            .post(RIDE_ENDPOINT)
            .then()
            .statusCode(HttpStatus.BAD_REQUEST.value())
            .extract()
            .response();
        ValidationResponse actual = response.as(ValidationResponse.class);
        assertThat(actual.errors()).hasSize(4);
    }

    @Test
    void updateRide_ReturnsUpdatedRideDto_UpdatedPickupAddressAndDestinationAddress() throws Exception {
        WireMockStubs.getPassengerResponseStub(wireMockServer, objectMapper, PASSENGER_RESPONSE);
        WireMockStubs.getDriverResponseStub(wireMockServer, objectMapper, DRIVER_RESPONSE);

        Response response = given()
            .contentType(ContentType.JSON)
            .body(UPDATED_RIDE_REQUEST)
            .when()
            .put(RIDE_BY_ID_ENDPOINT, RIDE_ID)
            .then()
            .statusCode(HttpStatus.OK.value())
            .extract()
            .response();
        RideResponse actual = response.as(RideResponse.class);
        assertThat(actual.pickupAddress()).isEqualTo(UPDATED_PICKUP_ADDRESS);
    }

    @Test
    void updateRide_ReturnsNotFound_DatabaseDoesNotContainSuchRideId() {
        given()
            .contentType(ContentType.JSON)
            .body(UPDATED_RIDE_REQUEST)
            .when()
            .put(RIDE_BY_ID_ENDPOINT, 999)
            .then()
            .statusCode(HttpStatus.NOT_FOUND.value());
    }

    @Test
    void updateRideStatus_ReturnsUpdatedRideDto_UpdatedRideStatus() {
        Response response = given()
            .contentType(ContentType.JSON)
            .body(RIDE_STATUS_REQUEST)
            .when()
            .patch(RIDE_STATUS_ENDPOINT, RIDE_ID)
            .then()
            .statusCode(HttpStatus.OK.value())
            .extract()
            .response();
        RideResponse actual = response.as(RideResponse.class);
        assertThat(actual.rideStatus()).isEqualTo(RIDE_STATUS_ACCEPTED);
    }

    @Test
    void updateRideStatus_ReturnsConflict_IncorrectRideStatus() {
        given()
            .contentType(ContentType.JSON)
            .body(INVALID_RIDE_STATUS_REQUEST)
            .when()
            .patch(RIDE_STATUS_ENDPOINT, RIDE_ID)
            .then()
            .statusCode(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    void deleteRideById_ReturnsNoContent_DatabaseContainsSuchRideId() {
        given()
            .when()
            .delete(RIDE_BY_ID_ENDPOINT, RIDE_ID)
            .then()
            .statusCode(HttpStatus.NO_CONTENT.value());
    }

    @Test
    void deleteRideById_ReturnsNotFound_DatabaseDoesNotContainSuchRideId() {
        given()
            .contentType(ContentType.JSON)
            .when()
            .delete(RIDE_BY_ID_ENDPOINT, 999)
            .then()
            .statusCode(HttpStatus.NOT_FOUND.value());
    }

    @Test
    void getAllRides_ReturnsPageWithRideDto_DefaultOffsetAndLimit() throws Exception {
        Response response = given()
            .when()
            .get(RIDE_ENDPOINT)
            .then()
            .statusCode(HttpStatus.OK.value())
            .extract()
            .response();
        PageResponse<RideResponse> actual = objectMapper.readValue(response.getBody().asString(), new TypeReference<>() {});
        assertThat(actual.values()).hasSize(1);
    }

    @Test
    void getAllRidesByDriver_ReturnsPageWithRideDto_DefaultOffsetAndLimit() throws Exception {
        WireMockStubs.getDriverResponseStub(wireMockServer, objectMapper, DRIVER_RESPONSE);

        Response response = given()
            .when()
            .get(RIDES_BY_DRIVER_ENDPOINT, DRIVER_ID)
            .then()
            .statusCode(HttpStatus.OK.value())
            .extract()
            .response();
        PageResponse<RideResponse> actual = objectMapper.readValue(response.getBody().asString(), new TypeReference<>() {});
        assertThat(actual.values()).hasSize(1);
    }

    @Test
    void getAllRidesByPassenger_ReturnsPageWithRideDto_DefaultOffsetAndLimit() throws Exception {
        WireMockStubs.getPassengerResponseStub(wireMockServer, objectMapper, PASSENGER_RESPONSE);

        Response response = given()
            .when()
            .get(RIDES_BY_PASSENGER_ENDPOINT, PASSENGER_ID)
            .then()
            .statusCode(HttpStatus.OK.value())
            .extract()
            .response();
        PageResponse<RideResponse> actual = objectMapper.readValue(response.getBody().asString(), new TypeReference<>() {});
        assertThat(actual.values()).hasSize(1);
    }

}