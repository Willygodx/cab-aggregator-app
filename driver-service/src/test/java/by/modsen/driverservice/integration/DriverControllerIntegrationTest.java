package by.modsen.driverservice.integration;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

import by.modsen.driverservice.constants.IntegrationTestDataConstants;
import by.modsen.driverservice.dto.request.DriverRequest;
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
    IntegrationTestDataConstants.SQL_DELETE_ALL_DRIVERS,
    IntegrationTestDataConstants.SQL_RESTART_DRIVER_SEQUENCES,
    IntegrationTestDataConstants.SQL_INSERT_DRIVER_DATA
}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class DriverControllerIntegrationTest {

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
    void getAllDrivers_ReturnsPageWithDrivers_DefaultOffsetAndLimit() {
        given()
            .when()
            .get(IntegrationTestDataConstants.DRIVER_ENDPOINT)
            .then()
            .statusCode(IntegrationTestDataConstants.HTTP_STATUS_OK)
            .contentType(ContentType.JSON)
            .body("currentOffset", equalTo(IntegrationTestDataConstants.DEFAULT_OFFSET))
            .body("currentLimit", equalTo(IntegrationTestDataConstants.DEFAULT_LIMIT))
            .body("totalPages", equalTo(IntegrationTestDataConstants.TOTAL_PAGES))
            .body("totalElements", equalTo(IntegrationTestDataConstants.TOTAL_ELEMENTS))
            .body("values[0].firstName", equalTo(IntegrationTestDataConstants.DRIVER_FIRST_NAME))
            .body("values[0].lastName", equalTo(IntegrationTestDataConstants.DRIVER_LAST_NAME))
            .body("values[0].email", equalTo(IntegrationTestDataConstants.DRIVER_EMAIL))
            .body("values[0].phoneNumber", equalTo(IntegrationTestDataConstants.DRIVER_PHONE_NUMBER))
            .body("values[0].sex", equalTo(IntegrationTestDataConstants.DRIVER_SEX));
    }

    @Test
    void getDriverById_ReturnsDriverResponse_WhenDriverExists() {
        given()
            .when()
            .get(IntegrationTestDataConstants.DRIVER_BY_ID_ENDPOINT, IntegrationTestDataConstants.DRIVER_ID)
            .then()
            .statusCode(IntegrationTestDataConstants.HTTP_STATUS_OK)
            .contentType(ContentType.JSON)
            .body("id", equalTo(IntegrationTestDataConstants.DRIVER_ID))
            .body("firstName", equalTo(IntegrationTestDataConstants.DRIVER_FIRST_NAME))
            .body("lastName", equalTo(IntegrationTestDataConstants.DRIVER_LAST_NAME))
            .body("email", equalTo(IntegrationTestDataConstants.DRIVER_EMAIL))
            .body("phoneNumber", equalTo(IntegrationTestDataConstants.DRIVER_PHONE_NUMBER))
            .body("sex", equalTo(IntegrationTestDataConstants.DRIVER_SEX));
    }

    @Test
    void createDriver_ReturnsDriverResponse_WhenDriverIsCreated() {
        DriverRequest driverRequest = new DriverRequest(
            IntegrationTestDataConstants.DRIVER_NEW_FIRST_NAME,
            IntegrationTestDataConstants.DRIVER_NEW_LAST_NAME,
            IntegrationTestDataConstants.DRIVER_NEW_EMAIL,
            IntegrationTestDataConstants.DRIVER_NEW_PHONE_NUMBER,
            IntegrationTestDataConstants.DRIVER_NEW_SEX
        );

        given()
            .contentType(ContentType.JSON)
            .body(driverRequest)
            .when()
            .post(IntegrationTestDataConstants.DRIVER_ENDPOINT)
            .then()
            .statusCode(IntegrationTestDataConstants.HTTP_STATUS_CREATED)
            .contentType(ContentType.JSON)
            .body("firstName", equalTo(IntegrationTestDataConstants.DRIVER_NEW_FIRST_NAME))
            .body("lastName", equalTo(IntegrationTestDataConstants.DRIVER_NEW_LAST_NAME))
            .body("email", equalTo(IntegrationTestDataConstants.DRIVER_NEW_EMAIL))
            .body("phoneNumber", equalTo(IntegrationTestDataConstants.DRIVER_NEW_PHONE_NUMBER))
            .body("sex", equalTo(IntegrationTestDataConstants.DRIVER_NEW_SEX));
    }

    @Test
    void updateDriverById_ReturnsUpdatedDriverResponse_WhenDriverIsUpdated() {
        DriverRequest driverRequest = new DriverRequest(
            null,
            null,
            null,
            IntegrationTestDataConstants.DRIVER_NEW_PHONE_NUMBER,
            null
        );

        given()
            .contentType(ContentType.JSON)
            .body(driverRequest)
            .when()
            .put(IntegrationTestDataConstants.DRIVER_BY_ID_ENDPOINT, IntegrationTestDataConstants.DRIVER_ID)
            .then()
            .statusCode(IntegrationTestDataConstants.HTTP_STATUS_OK)
            .contentType(ContentType.JSON)
            .body("id", equalTo(IntegrationTestDataConstants.DRIVER_ID))
            .body("firstName", equalTo(IntegrationTestDataConstants.DRIVER_FIRST_NAME))
            .body("lastName", equalTo(IntegrationTestDataConstants.DRIVER_LAST_NAME))
            .body("email", equalTo(IntegrationTestDataConstants.DRIVER_EMAIL))
            .body("phoneNumber", equalTo(IntegrationTestDataConstants.DRIVER_NEW_PHONE_NUMBER))
            .body("sex", equalTo(IntegrationTestDataConstants.DRIVER_SEX));
    }

    @Test
    void deleteDriverById_ReturnsNoContent_WhenDriverIsDeleted() {
        given()
            .when()
            .delete(IntegrationTestDataConstants.DRIVER_BY_ID_ENDPOINT, IntegrationTestDataConstants.DRIVER_ID)
            .then()
            .statusCode(IntegrationTestDataConstants.HTTP_STATUS_NO_CONTENT);
    }

    @Test
    void getDriverById_ReturnsNotFound_WhenDriverDoesNotExist() {
        given()
            .when()
            .get(IntegrationTestDataConstants.DRIVER_BY_ID_ENDPOINT, IntegrationTestDataConstants.NON_EXISTENT_DRIVER_ID)
            .then()
            .statusCode(IntegrationTestDataConstants.HTTP_STATUS_NOT_FOUND);
    }

    @Test
    void createDriver_ReturnsBadRequest_WhenDataIsInvalid() {
        DriverRequest invalidDriverRequest = new DriverRequest(
            IntegrationTestDataConstants.INVALID_FIRST_NAME,
            IntegrationTestDataConstants.DRIVER_NEW_LAST_NAME,
            IntegrationTestDataConstants.INVALID_EMAIL,
            IntegrationTestDataConstants.INVALID_PHONE_NUMBER,
            IntegrationTestDataConstants.INVALID_SEX
        );

        given()
            .contentType(ContentType.JSON)
            .body(invalidDriverRequest)
            .when()
            .post(IntegrationTestDataConstants.DRIVER_ENDPOINT)
            .then()
            .statusCode(IntegrationTestDataConstants.HTTP_STATUS_BAD_REQUEST);
    }

    @Test
    void updateDriverById_ReturnsNotFound_WhenDriverDoesNotExist() {
        DriverRequest driverRequest = new DriverRequest(
            null,
            null,
            null,
            IntegrationTestDataConstants.DRIVER_NEW_PHONE_NUMBER,
            null
        );

        given()
            .contentType(ContentType.JSON)
            .body(driverRequest)
            .when()
            .put(IntegrationTestDataConstants.DRIVER_BY_ID_ENDPOINT, IntegrationTestDataConstants.NON_EXISTENT_DRIVER_ID)
            .then()
            .statusCode(IntegrationTestDataConstants.HTTP_STATUS_NOT_FOUND);
    }

    @Test
    void deleteDriverById_ReturnsNotFound_WhenDriverDoesNotExist() {
        given()
            .when()
            .delete(IntegrationTestDataConstants.DRIVER_BY_ID_ENDPOINT, IntegrationTestDataConstants.NON_EXISTENT_DRIVER_ID)
            .then()
            .statusCode(IntegrationTestDataConstants.HTTP_STATUS_NOT_FOUND);
    }

    @Test
    void getAllDrivers_ReturnsBadRequest_WhenPaginationParametersAreInvalid() {
        given()
            .queryParam("offset", IntegrationTestDataConstants.INVALID_OFFSET)
            .queryParam("limit", IntegrationTestDataConstants.INVALID_LIMIT)
            .when()
            .get(IntegrationTestDataConstants.DRIVER_ENDPOINT)
            .then()
            .statusCode(IntegrationTestDataConstants.HTTP_STATUS_BAD_REQUEST);
    }

    @Test
    void addCarToDriver_ReturnsOk_WhenCarIsAdded() {
        given()
            .when()
            .post(IntegrationTestDataConstants.DRIVER_ENDPOINT + "/{driverId}/add-car/{carId}",
                IntegrationTestDataConstants.DRIVER_ID, IntegrationTestDataConstants.CAR_ID)
            .then()
            .statusCode(IntegrationTestDataConstants.HTTP_STATUS_OK);
    }

    @Test
    void addCarToDriver_ReturnsNotFound_WhenDriverDoesNotExist() {
        given()
            .when()
            .post(IntegrationTestDataConstants.DRIVER_ENDPOINT + "/{driverId}/add-car/{carId}",
                IntegrationTestDataConstants.NON_EXISTENT_DRIVER_ID, IntegrationTestDataConstants.CAR_ID)
            .then()
            .statusCode(IntegrationTestDataConstants.HTTP_STATUS_NOT_FOUND);
    }

    @Test
    void addCarToDriver_ReturnsNotFound_WhenCarDoesNotExist() {
        given()
            .when()
            .post(IntegrationTestDataConstants.DRIVER_ENDPOINT + "/{driverId}/add-car/{carId}",
                IntegrationTestDataConstants.DRIVER_ID, IntegrationTestDataConstants.NON_EXISTENT_CAR_ID)
            .then()
            .statusCode(IntegrationTestDataConstants.HTTP_STATUS_NOT_FOUND);
    }

}
