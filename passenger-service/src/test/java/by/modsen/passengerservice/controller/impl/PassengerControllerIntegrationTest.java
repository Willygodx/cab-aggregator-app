package by.modsen.passengerservice.controller.impl;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

import by.modsen.passengerservice.constants.IntegrationTestDataConstants;
import by.modsen.passengerservice.dto.request.PassengerRequest;
import by.modsen.passengerservice.kafka.consumer.RatingConsumer;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@MockBean(RatingConsumer.class)
@Sql(statements = {
    IntegrationTestDataConstants.SQL_DELETE_ALL_DATA,
    IntegrationTestDataConstants.SQL_RESTART_SEQUENCES,
    IntegrationTestDataConstants.SQL_INSERT_DATA
}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class PassengerControllerIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>(
        DockerImageName.parse(IntegrationTestDataConstants.POSTGRESQL_IMAGE_NAME)
    );

    @LocalServerPort
    private int port;

    @BeforeEach
    void setup() {
        RestAssured.port = this.port;
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
        registry.add(
            IntegrationTestDataConstants.EUREKA_CLIENT_ENABLED_PROPERTY,
            () -> IntegrationTestDataConstants.EUREKA_CLIENT_DISABLED_VALUE
        );
    }

    @Test
    void getAllPassengers_ReturnsPageWithPassengers_DefaultOffsetAndLimit() {
        given()
            .when()
            .get(IntegrationTestDataConstants.PASSENGER_ENDPOINT)
            .then()
            .statusCode(IntegrationTestDataConstants.HTTP_STATUS_OK)
            .contentType(ContentType.JSON)
            .body("currentOffset", equalTo(IntegrationTestDataConstants.DEFAULT_OFFSET))
            .body("currentLimit", equalTo(IntegrationTestDataConstants.DEFAULT_LIMIT))
            .body("totalPages", equalTo(IntegrationTestDataConstants.TOTAL_PAGES))
            .body("totalElements", equalTo(IntegrationTestDataConstants.TOTAL_ELEMENTS))
            .body("values[0].firstName", equalTo(IntegrationTestDataConstants.PASSENGER_FIRST_NAME))
            .body("values[0].lastName", equalTo(IntegrationTestDataConstants.PASSENGER_LAST_NAME))
            .body("values[0].email", equalTo(IntegrationTestDataConstants.PASSENGER_EMAIL))
            .body("values[0].phoneNumber", equalTo(IntegrationTestDataConstants.PASSENGER_PHONE_NUMBER));
    }

    @Test
    void getPassengerById_ReturnsPassengerResponse_WhenPassengerExists() {
        given()
            .when()
            .get(IntegrationTestDataConstants.PASSENGER_BY_ID_ENDPOINT, IntegrationTestDataConstants.PASSENGER_ID)
            .then()
            .statusCode(IntegrationTestDataConstants.HTTP_STATUS_OK)
            .contentType(ContentType.JSON)
            .body("id", equalTo(IntegrationTestDataConstants.PASSENGER_ID))
            .body("firstName", equalTo(IntegrationTestDataConstants.PASSENGER_FIRST_NAME))
            .body("lastName", equalTo(IntegrationTestDataConstants.PASSENGER_LAST_NAME))
            .body("email", equalTo(IntegrationTestDataConstants.PASSENGER_EMAIL))
            .body("phoneNumber", equalTo(IntegrationTestDataConstants.PASSENGER_PHONE_NUMBER));
    }

    @Test
    void createPassenger_ReturnsPassengerResponse_WhenPassengerIsCreated() {
        PassengerRequest passengerRequest = new PassengerRequest(
            IntegrationTestDataConstants.PASSENGER_NEW_FIRST_NAME,
            IntegrationTestDataConstants.PASSENGER_NEW_LAST_NAME,
            IntegrationTestDataConstants.PASSENGER_NEW_EMAIL,
            IntegrationTestDataConstants.PASSENGER_NEW_PHONE_NUMBER_FOR_CREATE
        );

        given()
            .contentType(ContentType.JSON)
            .body(passengerRequest)
            .when()
            .post(IntegrationTestDataConstants.PASSENGER_ENDPOINT)
            .then()
            .statusCode(IntegrationTestDataConstants.HTTP_STATUS_CREATED)
            .contentType(ContentType.JSON)
            .body("firstName", equalTo(IntegrationTestDataConstants.PASSENGER_NEW_FIRST_NAME))
            .body("lastName", equalTo(IntegrationTestDataConstants.PASSENGER_NEW_LAST_NAME))
            .body("email", equalTo(IntegrationTestDataConstants.PASSENGER_NEW_EMAIL))
            .body("phoneNumber", equalTo(IntegrationTestDataConstants.PASSENGER_NEW_PHONE_NUMBER_FOR_CREATE));
    }

    @Test
    void updatePassengerById_ReturnsUpdatedPassengerResponse_WhenPassengerIsUpdated() {
        PassengerRequest passengerRequest = new PassengerRequest(
            null,
            null,
            null,
            IntegrationTestDataConstants.PASSENGER_NEW_PHONE_NUMBER
        );

        given()
            .contentType(ContentType.JSON)
            .body(passengerRequest)
            .when()
            .put(IntegrationTestDataConstants.PASSENGER_BY_ID_ENDPOINT, IntegrationTestDataConstants.PASSENGER_ID)
            .then()
            .statusCode(IntegrationTestDataConstants.HTTP_STATUS_OK)
            .contentType(ContentType.JSON)
            .body("id", equalTo(IntegrationTestDataConstants.PASSENGER_ID))
            .body("firstName", equalTo(IntegrationTestDataConstants.PASSENGER_FIRST_NAME))
            .body("lastName", equalTo(IntegrationTestDataConstants.PASSENGER_LAST_NAME))
            .body("email", equalTo(IntegrationTestDataConstants.PASSENGER_EMAIL))
            .body("phoneNumber", equalTo(IntegrationTestDataConstants.PASSENGER_NEW_PHONE_NUMBER));
    }

    @Test
    void deletePassengerById_ReturnsNoContent_WhenPassengerIsDeleted() {
        given()
            .when()
            .delete(IntegrationTestDataConstants.PASSENGER_BY_ID_ENDPOINT, IntegrationTestDataConstants.PASSENGER_ID)
            .then()
            .statusCode(IntegrationTestDataConstants.HTTP_STATUS_NO_CONTENT);
    }

    @Test
    void getPassengerById_ReturnsNotFound_WhenPassengerDoesNotExist() {
        given()
            .when()
            .get(IntegrationTestDataConstants.PASSENGER_BY_ID_ENDPOINT,
                IntegrationTestDataConstants.NON_EXISTENT_PASSENGER_ID)
            .then()
            .statusCode(IntegrationTestDataConstants.HTTP_STATUS_NOT_FOUND);
    }

    @Test
    void createPassenger_ReturnsBadRequest_WhenDataIsInvalid() {
        PassengerRequest invalidPassengerRequest = new PassengerRequest(
            IntegrationTestDataConstants.INVALID_FIRST_NAME,
            IntegrationTestDataConstants.PASSENGER_NEW_LAST_NAME,
            IntegrationTestDataConstants.INVALID_EMAIL,
            IntegrationTestDataConstants.INVALID_PHONE_NUMBER
        );

        given()
            .contentType(ContentType.JSON)
            .body(invalidPassengerRequest)
            .when()
            .post(IntegrationTestDataConstants.PASSENGER_ENDPOINT)
            .then()
            .statusCode(IntegrationTestDataConstants.HTTP_STATUS_BAD_REQUEST);
    }

    @Test
    void updatePassengerById_ReturnsNotFound_WhenPassengerDoesNotExist() {
        PassengerRequest passengerRequest = new PassengerRequest(
            null,
            null,
            null,
            IntegrationTestDataConstants.PASSENGER_NEW_PHONE_NUMBER
        );

        given()
            .contentType(ContentType.JSON)
            .body(passengerRequest)
            .when()
            .put(IntegrationTestDataConstants.PASSENGER_BY_ID_ENDPOINT,
                IntegrationTestDataConstants.NON_EXISTENT_PASSENGER_ID)
            .then()
            .statusCode(IntegrationTestDataConstants.HTTP_STATUS_NOT_FOUND);
    }

    @Test
    void deletePassengerById_ReturnsNotFound_WhenPassengerDoesNotExist() {
        given()
            .when()
            .delete(IntegrationTestDataConstants.PASSENGER_BY_ID_ENDPOINT,
                IntegrationTestDataConstants.NON_EXISTENT_PASSENGER_ID)
            .then()
            .statusCode(IntegrationTestDataConstants.HTTP_STATUS_NOT_FOUND);
    }

    @Test
    void getAllPassengers_ReturnsBadRequest_WhenPaginationParametersAreInvalid() {
        given()
            .queryParam("offset", IntegrationTestDataConstants.INVALID_OFFSET)
            .queryParam("limit", IntegrationTestDataConstants.INVALID_LIMIT)
            .when()
            .get(IntegrationTestDataConstants.PASSENGER_ENDPOINT)
            .then()
            .statusCode(IntegrationTestDataConstants.HTTP_STATUS_BAD_REQUEST);
    }

}