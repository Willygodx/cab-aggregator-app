package by.modsen.ratingservice.controller.impl;

import static by.modsen.ratingservice.constants.IntegrationTestDataConstants.*;
import static by.modsen.ratingservice.constants.TestDataConstants.DRIVER_RESPONSE;
import static by.modsen.ratingservice.constants.TestDataConstants.PASSENGER_RESPONSE;
import static by.modsen.ratingservice.constants.TestDataConstants.RATING_REQUEST;
import static by.modsen.ratingservice.constants.TestDataConstants.RATING_REQUEST_FOR_UPDATE;
import static by.modsen.ratingservice.constants.TestDataConstants.RIDE_RESPONSE;
import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

import by.modsen.ratingservice.dto.response.AverageRatingResponse;
import by.modsen.ratingservice.dto.response.PageResponse;
import by.modsen.ratingservice.dto.response.RatingResponse;
import by.modsen.ratingservice.exception.validation.ValidationResponse;
import by.modsen.ratingservice.kafka.producer.KafkaProducerConfig;
import by.modsen.ratingservice.kafka.producer.RatingProducer;
import by.modsen.ratingservice.wiremock.WireMockStubs;
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
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@MockBean({RatingProducer.class, KafkaProducerConfig.class})
@ActiveProfiles("test")
@AutoConfigureWireMock(port = 9091)
class RatingControllerIntegrationTest {

    @DynamicPropertySource
    private static void disableEureka(DynamicPropertyRegistry registry) {
        registry.add(EUREKA_CLIENT_ENABLED_PROPERTY, () -> BOOLEAN_PROPERTY_VALUE);
    }

    @ServiceConnection
    @Container
    static MongoDBContainer mongoDBContainer = new MongoDBContainer(DockerImageName.parse(MONGODB_IMAGE_NAME));

    @LocalServerPort
    private int port;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private WireMockServer wireMockServer;

    @Autowired
    private MongoTemplate mongoTemplate;

    @BeforeEach
    void setup() {
        RestAssured.port = this.port;
        mongoTemplate.dropCollection(DATABASE_NAME);

        mongoTemplate.insert(BEFORE_EACH_RATING);
    }

    @Test
    void getRatingById_ReturnsRatingDto_DatabaseContainsSuchRatingId() {
        Response response = given()
            .when()
            .get(RATING_BY_ID_ENDPOINT, RATING_ID)
            .then()
            .contentType(ContentType.JSON)
            .statusCode(HttpStatus.OK.value())
            .extract()
            .response();
        RatingResponse actual = response.as(RatingResponse.class);
        assertThat(actual.id()).isEqualTo(RATING_ID);
    }

    @Test
    void getRatingById_ReturnsNotFound_DatabaseDoesNotContainSuchRatingId() {
        given()
            .when()
            .get(RATING_BY_ID_ENDPOINT, "999")
            .then()
            .contentType(ContentType.JSON)
            .statusCode(HttpStatus.NOT_FOUND.value());
    }

    @Test
    void createRating_ReturnsCreatedRatingDto_AllMandatoryFieldsInRequestBody() throws Exception {
        WireMockStubs.getDriverResponseStub(wireMockServer, objectMapper, DRIVER_RESPONSE);
        WireMockStubs.getPassengerResponseStub(wireMockServer, objectMapper, PASSENGER_RESPONSE);
        WireMockStubs.getRideResponseStub(wireMockServer, objectMapper, RIDE_RESPONSE);

        Response response = given()
            .contentType(ContentType.JSON)
            .body(RATING_REQUEST)
            .when()
            .post(RATING_ENDPOINT)
            .then()
            .statusCode(HttpStatus.CREATED.value())
            .extract()
            .response();
        RatingResponse actual = response.as(RatingResponse.class);
        assertThat(actual.rideId()).isEqualTo(RIDE_ID);
    }

    @Test
    void createRating_ReturnsBadRequest_MissingMandatoryFields() {
        Response response = given()
            .contentType(ContentType.JSON)
            .body(INVALID_RATING_REQUEST)
            .when()
            .post(RATING_ENDPOINT)
            .then()
            .statusCode(HttpStatus.BAD_REQUEST.value())
            .extract()
            .response();
        ValidationResponse actual = response.as(ValidationResponse.class);
        assertThat(actual.errors()).hasSize(1);
    }

    @Test
    void updateRating_ReturnsUpdatedRatingDto_UpdatedMarkAndComment() throws Exception {
        WireMockStubs.getDriverResponseStub(wireMockServer, objectMapper, DRIVER_RESPONSE);
        WireMockStubs.getPassengerResponseStub(wireMockServer, objectMapper, PASSENGER_RESPONSE);
        WireMockStubs.getRideResponseStub(wireMockServer, objectMapper, RIDE_RESPONSE);

        Response response = given()
            .contentType(ContentType.JSON)
            .body(RATING_REQUEST_FOR_UPDATE)
            .when()
            .put(RATING_BY_ID_ENDPOINT, RATING_ID)
            .then()
            .statusCode(HttpStatus.OK.value())
            .extract()
            .response();
        RatingResponse actual = response.as(RatingResponse.class);
        assertThat(actual.mark()).isEqualTo(UPDATED_MARK);
    }

    @Test
    void updateRating_ReturnsNotFound_DatabaseDoesNotContainSuchRatingId() {
        given()
            .contentType(ContentType.JSON)
            .body(RATING_REQUEST_FOR_UPDATE)
            .when()
            .put(RATING_BY_ID_ENDPOINT, "999")
            .then()
            .statusCode(HttpStatus.NOT_FOUND.value());
    }

    @Test
    void deleteRatingById_ReturnsNoContent_DatabaseContainsSuchRatingId() {
        given()
            .when()
            .delete(RATING_BY_ID_ENDPOINT, RATING_ID)
            .then()
            .statusCode(HttpStatus.NO_CONTENT.value());
    }

    @Test
    void deleteRatingById_ReturnsNotFound_DatabaseDoesNotContainSuchRatingId() {
        given()
            .contentType(ContentType.JSON)
            .when()
            .delete(RATING_BY_ID_ENDPOINT, "999")
            .then()
            .statusCode(HttpStatus.NOT_FOUND.value());
    }

    @Test
    void getAllRatings_ReturnsPageWithRatingDto_DefaultOffsetAndLimit() throws Exception {
        Response response = given()
            .when()
            .get(RATING_ENDPOINT)
            .then()
            .statusCode(HttpStatus.OK.value())
            .extract()
            .response();
        PageResponse<RatingResponse> actual = objectMapper.readValue(response.getBody().asString(), new TypeReference<>() {});
        assertThat(actual.values()).hasSize(1);
    }

    @Test
    void getAllRatingsByDriver_ReturnsPageWithRatingDto_DefaultOffsetAndLimit() throws Exception {
        WireMockStubs.getDriverResponseStub(wireMockServer, objectMapper, DRIVER_RESPONSE);

        Response response = given()
            .when()
            .get(RATINGS_BY_DRIVER_ENDPOINT, DRIVER_ID)
            .then()
            .statusCode(HttpStatus.OK.value())
            .extract()
            .response();
        PageResponse<RatingResponse> actual = objectMapper.readValue(response.getBody().asString(), new TypeReference<>() {});
        assertThat(actual.values()).hasSize(1);
    }

    @Test
    void getAllRatingsByPassenger_ReturnsPageWithRatingDto_DefaultOffsetAndLimit() throws Exception {
        WireMockStubs.getPassengerResponseStub(wireMockServer, objectMapper, PASSENGER_RESPONSE);

        Response response = given()
            .when()
            .get(RATINGS_BY_PASSENGER_ENDPOINT, PASSENGER_ID)
            .then()
            .statusCode(HttpStatus.OK.value())
            .extract()
            .response();
        PageResponse<RatingResponse> actual = objectMapper.readValue(response.getBody().asString(), new TypeReference<>() {});
        assertThat(actual.values()).isEmpty();
    }

    @Test
    void getAverageRatingForDriver_ReturnsAverageRating_DatabaseContainsRatingsForDriver() {
        Response response = given()
            .when()
            .get(DRIVER_AVERAGE_RATING_ENDPOINT, DRIVER_ID)
            .then()
            .contentType(ContentType.JSON)
            .statusCode(HttpStatus.OK.value())
            .extract()
            .response();
        AverageRatingResponse actual = response.as(AverageRatingResponse.class);
        assertThat(actual.averageRating()).isEqualTo(0.0);
    }

    @Test
    void getAverageRatingForPassenger_ReturnsAverageRating_DatabaseContainsRatingsForPassenger() {
        Response response = given()
            .when()
            .get(PASSENGER_AVERAGE_RATING_ENDPOINT, PASSENGER_ID)
            .then()
            .contentType(ContentType.JSON)
            .statusCode(HttpStatus.OK.value())
            .extract()
            .response();
        AverageRatingResponse actual = response.as(AverageRatingResponse.class);
        assertThat(actual.averageRating()).isEqualTo(AVERAGE_RATING);
    }

}