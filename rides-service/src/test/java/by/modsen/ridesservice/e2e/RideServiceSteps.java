package by.modsen.ridesservice.e2e;

import by.modsen.ridesservice.constants.E2ETestDataConstants;
import by.modsen.ridesservice.dto.request.RideRequest;
import by.modsen.ridesservice.dto.request.RideStatusRequest;
import by.modsen.ridesservice.dto.response.PageResponse;
import by.modsen.ridesservice.dto.response.RideResponse;
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
import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

public class RideServiceSteps {

    private static final ObjectMapper objectMapper = new ObjectMapper()
        .registerModule(new JavaTimeModule());

    private Response actual;

    private RideRequest rideRequest;

    private RideStatusRequest rideStatusRequest;

    @Given("There is the following ride details")
    public void prepareRideDetails(String rideRequestJson) throws Exception {
        rideRequest = objectMapper.readValue(rideRequestJson, RideRequest.class);
    }

    @Given("There is the following invalid ride details")
    public void prepareInvalidRideDetails(String rideRequestJson) throws Exception {
        rideRequest = objectMapper.readValue(rideRequestJson, RideRequest.class);
    }

    @Given("There is the following ride status details")
    public void prepareRideStatusDetails(String rideStatusRequestJson) throws Exception {
        rideStatusRequest = objectMapper.readValue(rideStatusRequestJson, RideStatusRequest.class);
    }

    @Given("There is the following invalid ride status details")
    public void prepareInvalidRideStatusDetails(String rideStatusRequestJson) throws Exception {
        rideStatusRequest = objectMapper.readValue(rideStatusRequestJson, RideStatusRequest.class);
    }

    @When("Creates the ride")
    public void createRide() {
        actual = given()
            .contentType(ContentType.JSON)
            .body(rideRequest)
            .when()
            .post(E2ETestDataConstants.BASE_URL);
    }

    @Then("the response status is {int}")
    public void responseStatusShouldBe(int expectedStatus) {
        actual
            .then()
            .statusCode(expectedStatus);
    }

    @And("the response body should contain the information about created ride")
    public void responseBodyShouldContainTheInformationAboutCreatedRide(String rideResponseJson)
        throws Exception {
        RideResponse expectedResponse = objectMapper.readValue(rideResponseJson, RideResponse.class);
        RideResponse actualResponse = actual.as(RideResponse.class);

        assertThat(actualResponse)
            .usingRecursiveComparison()
            .ignoringFields("cost", "orderDateTime")
            .isEqualTo(expectedResponse);
    }

    @When("Update ride with id {int}")
    public void updateRideWithId(int id) {
        actual = given()
            .contentType(ContentType.JSON)
            .body(rideRequest)
            .when()
            .put(E2ETestDataConstants.BASE_URL + E2ETestDataConstants.ID_POSTFIX, id);
    }

    @And("the response body should contain the information about updated ride")
    public void responseBodyShouldContainTheInformationAboutUpdatedRide(String rideResponseJson)
        throws Exception {
        RideResponse expectedResponse = objectMapper.readValue(rideResponseJson, RideResponse.class);
        RideResponse actualResponse = actual.as(RideResponse.class);

        assertThat(actualResponse)
            .usingRecursiveComparison()
            .ignoringFields("cost", "orderDateTime")
            .isEqualTo(expectedResponse);
    }

    @When("Update ride status with id {int}")
    public void updateRideStatusWithId(int id) {
        actual = given()
            .contentType(ContentType.JSON)
            .body(rideStatusRequest)
            .when()
            .patch(E2ETestDataConstants.BASE_URL + E2ETestDataConstants.STATUS_POSTFIX, id);
    }

    @When("Get a page with rides with current offset {int} and limit {int}")
    public void getPageWithRidesWithCurrentOffsetAndLimit(int offset, int limit) {
        actual = given()
            .when()
            .get(E2ETestDataConstants.BASE_URL
                + E2ETestDataConstants.OFFSET_PARAM
                + offset
                + E2ETestDataConstants.LIMIT_PARAM
                + limit);
    }

    @And("the response body should contain the information about first ten rides")
    public void responseBodyShouldContainInformationAboutFirstTenRides(String rideResponseJson) throws Exception {
        PageResponse<RideResponse> expectedResponse = objectMapper.readValue(rideResponseJson, new TypeReference<>() {});
        PageResponse<RideResponse> actualResponse = objectMapper.readValue(
            actual.asString(),
            new TypeReference<>() {}
        );

        assertThat(actualResponse)
            .usingRecursiveComparison()
            .ignoringFields("values.cost", "values.orderDateTime")
            .isEqualTo(expectedResponse);
    }

    @When("Get ride with id {int}")
    public void getRideWithId(int id) {
        actual = given()
            .when()
            .get(E2ETestDataConstants.BASE_URL + E2ETestDataConstants.ID_POSTFIX, id);
    }

    @And("the response body should contain the information about ride")
    public void responseBodyShouldContainTheInformationAboutRide(String rideResponseJson)
        throws Exception {
        RideResponse expectedResponse = objectMapper.readValue(rideResponseJson, RideResponse.class);
        RideResponse actualResponse = actual.as(RideResponse.class);

        assertThat(actualResponse)
            .usingRecursiveComparison()
            .ignoringFields("cost", "orderDateTime")
            .isEqualTo(expectedResponse);
    }

    @When("Delete ride with id {int}")
    public void deleteRideWithId(int id) {
        actual = given()
            .when()
            .delete(E2ETestDataConstants.BASE_URL + E2ETestDataConstants.ID_POSTFIX, id);
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

}