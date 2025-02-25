package by.modsen.driverservice.integration;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

import by.modsen.driverservice.constants.IntegrationTestDataConstants;
import by.modsen.driverservice.dto.request.CarRequest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
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
@Sql(statements = {
    IntegrationTestDataConstants.SQL_DELETE_DRIVER_CAR,
    IntegrationTestDataConstants.SQL_DELETE_ALL_CARS,
    IntegrationTestDataConstants.SQL_RESTART_CAR_SEQUENCES,
    IntegrationTestDataConstants.SQL_INSERT_CAR_DATA
}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class CarControllerIntegrationTest {

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
    void getAllCars_ReturnsPageWithCars_DefaultOffsetAndLimit() {
        given()
            .when()
            .get(IntegrationTestDataConstants.CAR_ENDPOINT)
            .then()
            .statusCode(IntegrationTestDataConstants.HTTP_STATUS_OK)
            .contentType(ContentType.JSON)
            .body("currentOffset", equalTo(IntegrationTestDataConstants.DEFAULT_OFFSET))
            .body("currentLimit", equalTo(IntegrationTestDataConstants.DEFAULT_LIMIT))
            .body("totalPages", equalTo(IntegrationTestDataConstants.TOTAL_PAGES))
            .body("totalElements", equalTo(IntegrationTestDataConstants.TOTAL_ELEMENTS))
            .body("values[0].color", equalTo(IntegrationTestDataConstants.CAR_COLOR))
            .body("values[0].carBrand", equalTo(IntegrationTestDataConstants.CAR_BRAND))
            .body("values[0].carNumber", equalTo(IntegrationTestDataConstants.CAR_NUMBER));
    }

    @Test
    void getCarById_ReturnsCarResponse_WhenCarExists() {
        given()
            .when()
            .get(IntegrationTestDataConstants.CAR_BY_ID_ENDPOINT, IntegrationTestDataConstants.CAR_ID)
            .then()
            .statusCode(IntegrationTestDataConstants.HTTP_STATUS_OK)
            .contentType(ContentType.JSON)
            .body("id", equalTo(IntegrationTestDataConstants.CAR_ID))
            .body("color", equalTo(IntegrationTestDataConstants.CAR_COLOR))
            .body("carBrand", equalTo(IntegrationTestDataConstants.CAR_BRAND))
            .body("carNumber", equalTo(IntegrationTestDataConstants.CAR_NUMBER));
    }

    @Test
    void createCar_ReturnsCarResponse_WhenCarIsCreated() {
        CarRequest carRequest = new CarRequest(
            IntegrationTestDataConstants.CAR_NEW_COLOR,
            IntegrationTestDataConstants.CAR_NEW_BRAND,
            IntegrationTestDataConstants.CAR_NEW_NUMBER_FOR_CREATE
        );

        given()
            .contentType(ContentType.JSON)
            .body(carRequest)
            .when()
            .post(IntegrationTestDataConstants.CAR_ENDPOINT)
            .then()
            .statusCode(IntegrationTestDataConstants.HTTP_STATUS_CREATED)
            .contentType(ContentType.JSON)
            .body("color", equalTo(IntegrationTestDataConstants.CAR_NEW_COLOR))
            .body("carBrand", equalTo(IntegrationTestDataConstants.CAR_NEW_BRAND))
            .body("carNumber", equalTo(IntegrationTestDataConstants.CAR_NEW_NUMBER_FOR_CREATE));
    }

    @Test
    void updateCarById_ReturnsUpdatedCarResponse_WhenCarIsUpdated() {
        CarRequest carRequest = new CarRequest(
            null,
            null,
            IntegrationTestDataConstants.CAR_NEW_NUMBER
        );

        given()
            .contentType(ContentType.JSON)
            .body(carRequest)
            .when()
            .put(IntegrationTestDataConstants.CAR_BY_ID_ENDPOINT, IntegrationTestDataConstants.CAR_ID)
            .then()
            .statusCode(IntegrationTestDataConstants.HTTP_STATUS_OK)
            .contentType(ContentType.JSON)
            .body("id", equalTo(IntegrationTestDataConstants.CAR_ID))
            .body("color", equalTo(IntegrationTestDataConstants.CAR_COLOR))
            .body("carBrand", equalTo(IntegrationTestDataConstants.CAR_BRAND))
            .body("carNumber", equalTo(IntegrationTestDataConstants.CAR_NEW_NUMBER));
    }

    @Test
    void deleteCarById_ReturnsNoContent_WhenCarIsDeleted() {
        given()
            .when()
            .delete(IntegrationTestDataConstants.CAR_BY_ID_ENDPOINT, IntegrationTestDataConstants.CAR_ID)
            .then()
            .statusCode(IntegrationTestDataConstants.HTTP_STATUS_NO_CONTENT);
    }

    @Test
    void getCarById_ReturnsNotFound_WhenCarDoesNotExist() {
        given()
            .when()
            .get(IntegrationTestDataConstants.CAR_BY_ID_ENDPOINT,
                IntegrationTestDataConstants.NON_EXISTENT_CAR_ID)
            .then()
            .statusCode(IntegrationTestDataConstants.HTTP_STATUS_NOT_FOUND);
    }

    @Test
    void createCar_ReturnsBadRequest_WhenDataIsInvalid() {
        CarRequest invalidCarRequest = new CarRequest(
            IntegrationTestDataConstants.INVALID_COLOR,
            IntegrationTestDataConstants.CAR_NEW_BRAND,
            IntegrationTestDataConstants.INVALID_NUMBER
        );

        given()
            .contentType(ContentType.JSON)
            .body(invalidCarRequest)
            .when()
            .post(IntegrationTestDataConstants.CAR_ENDPOINT)
            .then()
            .statusCode(IntegrationTestDataConstants.HTTP_STATUS_BAD_REQUEST);
    }

    @Test
    void updateCarById_ReturnsNotFound_WhenCarDoesNotExist() {
        CarRequest carRequest = new CarRequest(
            null,
            null,
            IntegrationTestDataConstants.CAR_NEW_NUMBER
        );

        given()
            .contentType(ContentType.JSON)
            .body(carRequest)
            .when()
            .put(IntegrationTestDataConstants.CAR_BY_ID_ENDPOINT,
                IntegrationTestDataConstants.NON_EXISTENT_CAR_ID)
            .then()
            .statusCode(IntegrationTestDataConstants.HTTP_STATUS_NOT_FOUND);
    }

    @Test
    void deleteCarById_ReturnsNotFound_WhenCarDoesNotExist() {
        given()
            .when()
            .delete(IntegrationTestDataConstants.CAR_BY_ID_ENDPOINT,
                IntegrationTestDataConstants.NON_EXISTENT_CAR_ID)
            .then()
            .statusCode(IntegrationTestDataConstants.HTTP_STATUS_NOT_FOUND);
    }

    @Test
    void getAllCars_ReturnsBadRequest_WhenPaginationParametersAreInvalid() {
        given()
            .queryParam("offset", IntegrationTestDataConstants.INVALID_OFFSET)
            .queryParam("limit", IntegrationTestDataConstants.INVALID_LIMIT)
            .when()
            .get(IntegrationTestDataConstants.CAR_ENDPOINT)
            .then()
            .statusCode(IntegrationTestDataConstants.HTTP_STATUS_BAD_REQUEST);
    }

}