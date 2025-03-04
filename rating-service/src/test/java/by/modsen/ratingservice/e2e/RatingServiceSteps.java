package by.modsen.ratingservice.e2e;

import by.modsen.ratingservice.constants.E2ETestDataConstants;
import by.modsen.ratingservice.dto.request.RatingRequest;
import by.modsen.ratingservice.dto.response.AverageRatingResponse;
import by.modsen.ratingservice.dto.response.PageResponse;
import by.modsen.ratingservice.dto.response.RatingResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.RequiredArgsConstructor;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

@RequiredArgsConstructor
public class RatingServiceSteps {

    private static final ObjectMapper objectMapper = new ObjectMapper()
        .registerModule(new JavaTimeModule());

    private Response actual;

    private RatingRequest ratingRequest;

    private final E2ETestContext testContext;

    @Given("There is the following rating details")
    public void prepareRatingDetails(String ratingRequestJson) throws Exception {
        ratingRequest = objectMapper.readValue(ratingRequestJson, RatingRequest.class);
    }

    @Given("There is the following invalid rating details")
    public void prepareInvalidRatingDetails(String ratingRequestJson) throws Exception {
        ratingRequest = objectMapper.readValue(ratingRequestJson, RatingRequest.class);
    }

    @When("Creates the rating")
    public void createRating() {
        actual = given()
            .contentType(ContentType.JSON)
            .body(ratingRequest)
            .when()
            .post(E2ETestDataConstants.BASE_URL);
    }

    @Then("the response status is {int}")
    public void responseStatusShouldBe(int expectedStatus) {
        actual.then().statusCode(expectedStatus);
    }

    @And("save the response body as {string}")
    public void saveResponseBodyAs(String variableName) throws Exception {
        if ("createdRating".equals(variableName)) {
            RatingResponse ratingResponse = actual.as(RatingResponse.class);
            testContext.setCreatedRatingId(ratingResponse.id());
        }
    }

    @And("the response body should contain the information about created rating")
    public void responseBodyShouldContainTheInformationAboutCreatedRating(String ratingResponseJson)
        throws Exception {
        RatingResponse expectedResponse = objectMapper.readValue(ratingResponseJson, RatingResponse.class);
        RatingResponse actualResponse = actual.as(RatingResponse.class);

        assertThat(actualResponse)
            .usingRecursiveComparison()
            .ignoringFields("id")
            .isEqualTo(expectedResponse);
    }

    @When("Get rating with id {string}")
    public void getRatingWithId(String id) {
        String ratingId = id.equals("createdRating") ? testContext.getCreatedRatingId() : id;
        actual = given()
            .when()
            .get(E2ETestDataConstants.BASE_URL + E2ETestDataConstants.ID_POSTFIX, ratingId);
    }

    @And("the response body should contain the information about rating")
    public void responseBodyShouldContainTheInformationAboutRating(String ratingResponseJson)
        throws Exception {
        RatingResponse expectedResponse = objectMapper.readValue(ratingResponseJson, RatingResponse.class);
        RatingResponse actualResponse = actual.as(RatingResponse.class);

        assertThat(actualResponse)
            .usingRecursiveComparison()
            .ignoringFields("id")
            .isEqualTo(expectedResponse);
    }

    @When("Get a page with ratings with current offset {int} and limit {int}")
    public void getPageWithRatingsWithCurrentOffsetAndLimit(int offset, int limit) {
        actual = given()
            .when()
            .get(E2ETestDataConstants.BASE_URL
                + E2ETestDataConstants.OFFSET_PARAM
                + offset
                + E2ETestDataConstants.LIMIT_PARAM
                + limit);
    }

    @And("the response body should contain the information about first ten ratings")
    public void responseBodyShouldContainInformationAboutFirstTenRatings(String ratingResponseJson) throws Exception {
        PageResponse<RatingResponse> expectedResponse = objectMapper.readValue(ratingResponseJson, new TypeReference<>() {});
        PageResponse<RatingResponse> actualResponse = objectMapper.readValue(
            actual.asString(),
            new TypeReference<>() {}
        );

        assertThat(actualResponse)
            .usingRecursiveComparison()
            .ignoringFields("values.id")
            .isEqualTo(expectedResponse);
    }

    @When("Get average rating for passenger with id {int}")
    public void getAverageRatingForPassengerWithId(int passengerId) {
        actual = given()
            .when()
            .get(E2ETestDataConstants.BASE_URL + E2ETestDataConstants.PASSENGER_AVERAGE_RATING_POSTFIX, passengerId);
    }

    @And("the response body should contain the average rating")
    public void responseBodyShouldContainTheAverageRating(String averageRatingResponseJson) throws Exception {
        AverageRatingResponse expectedResponse = objectMapper.readValue(averageRatingResponseJson, AverageRatingResponse.class);
        AverageRatingResponse actualResponse = actual.as(AverageRatingResponse.class);

        assertThat(actualResponse).isEqualTo(expectedResponse);
    }

    @When("Get average rating for driver with id {int}")
    public void getAverageRatingForDriverWithId(int driverId) {
        actual = given()
            .when()
            .get(E2ETestDataConstants.BASE_URL + E2ETestDataConstants.DRIVER_AVERAGE_RATING_POSTFIX, driverId);
    }

    @When("Trigger Kafka to calculate and send average ratings")
    public void triggerKafkaToCalculateAndSendAverageRatings() {
        actual = given()
            .when()
            .post(E2ETestDataConstants.BASE_URL + E2ETestDataConstants.TRIGGER_KAFKA_POSTFIX);
    }

    @When("Update rating with id {string}")
    public void updateRatingWithId(String id) {
        String ratingId = id.equals("createdRating") ? testContext.getCreatedRatingId() : id;
        actual = given()
            .contentType(ContentType.JSON)
            .body(ratingRequest)
            .when()
            .put(E2ETestDataConstants.BASE_URL + E2ETestDataConstants.ID_POSTFIX, ratingId);
    }

    @And("the response body should contain the information about updated rating")
    public void responseBodyShouldContainTheInformationAboutUpdatedRating(String ratingResponseJson)
        throws Exception {
        RatingResponse expectedResponse = objectMapper.readValue(ratingResponseJson, RatingResponse.class);
        RatingResponse actualResponse = actual.as(RatingResponse.class);

        assertThat(actualResponse)
            .usingRecursiveComparison()
            .ignoringFields("id")
            .isEqualTo(expectedResponse);
    }

    @When("Delete rating with id {string}")
    public void deleteRatingWithId(String id) {
        String ratingId = id.equals("createdRating") ? testContext.getCreatedRatingId() : id;
        actual = given()
            .when()
            .delete(E2ETestDataConstants.BASE_URL + E2ETestDataConstants.ID_POSTFIX, ratingId);
    }

    @And("the response body should contain the validation errors")
    public void responseBodyShouldContainTheValidationErrors(String expectedResponseJson) throws Exception {
        Map<String, Object> actualMap = objectMapper.readValue(actual.asString(), new TypeReference<>() {});
        Map<String, Object> expectedMap = objectMapper.readValue(expectedResponseJson, new TypeReference<>() {});

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> actualErrors = (List<Map<String, Object>>) actualMap.remove("errors");
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> expectedErrors = (List<Map<String, Object>>) expectedMap.remove("errors");

        assertThat(actualMap).isEqualTo(expectedMap);

        if (actualErrors != null && expectedErrors != null) {
            assertThat(actualErrors).containsExactlyInAnyOrderElementsOf(expectedErrors);
        } else {
            assertThat(actualErrors).isEqualTo(expectedErrors);
        }
    }

    @And("the response body should contain the error message")
    public void theResponseBodyShouldContainTheErrorMessage(String expectedResponseJson) throws Exception {
        Map<String, Object> actualMap = objectMapper.readValue(actual.asString(), new TypeReference<>() {});
        Map<String, Object> expectedMap = objectMapper.readValue(expectedResponseJson, new TypeReference<>() {});

        actualMap.remove("timestamp");
        expectedMap.remove("timestamp");

        assertThat(actualMap).isEqualTo(expectedMap);
    }

    @When("Delete rating with id from {string}")
    public void deleteRatingWithIdFrom(String variableName) {
        String ratingId = variableName.equals("createdRating") ? testContext.getCreatedRatingId() : variableName;
        actual = given()
            .when()
            .delete(E2ETestDataConstants.BASE_URL + E2ETestDataConstants.ID_POSTFIX, ratingId);
    }

    @When("Update rating with id from {string}")
    public void updateRatingWithIdFrom(String variableName) {
        String ratingId = variableName.equals("createdRating") ? testContext.getCreatedRatingId() : variableName;
        actual = given()
            .contentType(ContentType.JSON)
            .body(ratingRequest)
            .when()
            .put(E2ETestDataConstants.BASE_URL + E2ETestDataConstants.ID_POSTFIX, ratingId);
    }

}