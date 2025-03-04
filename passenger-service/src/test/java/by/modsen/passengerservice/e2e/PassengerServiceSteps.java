package by.modsen.passengerservice.e2e;

import by.modsen.passengerservice.constants.E2ETestDataConstants;
import by.modsen.passengerservice.dto.request.PassengerRequest;
import by.modsen.passengerservice.dto.response.PageResponse;
import by.modsen.passengerservice.dto.response.PassengerResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
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

public class PassengerServiceSteps {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private Response actual;

    private PassengerRequest passengerRequest;

    @Given("There is the following passenger details")
    public void preparePassengerDetails(String passengerRequestJson) throws Exception {
        passengerRequest = objectMapper.readValue(passengerRequestJson, PassengerRequest.class);
    }

    @When("Creates the passenger")
    public void createPassenger() {
        actual = given()
            .contentType(ContentType.JSON)
            .body(passengerRequest)
            .when()
            .post(E2ETestDataConstants.BASE_URL);
    }

    @Then("the response status is {int}")
    public void responseStatusShouldBeCreated(int expectedStatus) {
        actual
            .then()
            .statusCode(expectedStatus);
    }

    @Then("the response body should contain the information about created passenger")
    public void responseBodyShouldContainTheInformationAboutCreatedPassenger(String passengerResponseJson)
        throws Exception {
        assertThat(actual.as(PassengerResponse.class))
            .isEqualTo(objectMapper.readValue(passengerResponseJson, PassengerResponse.class));
    }

    @When("Get a page with passengers with current offset {int} and limit {int}")
    public void getPageWithPassengersWithCurrentOffsetAndLimit(int offset, int limit) {
        actual = given()
            .when()
            .get(E2ETestDataConstants.BASE_URL
                + E2ETestDataConstants.OFFSET_PARAM
                + offset
                + E2ETestDataConstants.LIMIT_PARAM
                + limit);
    }

    @And("the response body should contain the information about first ten passengers")
    public void responseBodyShouldContainInformationAboutFirstTenPassengers(String passengerResponseJson)
        throws Exception {
        assertThat(actual.as(PageResponse.class))
            .isEqualTo(objectMapper.readValue(passengerResponseJson, PageResponse.class));
    }

    @When("Get passenger with id {int}")
    public void getPassengerWithId(int id) {
        actual = given()
            .when()
            .get(E2ETestDataConstants.BASE_URL + E2ETestDataConstants.ID_POSTFIX, id);
    }

    @And("the response body should contain the information about passenger")
    public void responseBodyShouldContainTheInformationAboutPassenger(String passengerResponseJson)
        throws Exception {
        assertThat(actual.as(PassengerResponse.class))
            .isEqualTo(objectMapper.readValue(passengerResponseJson, PassengerResponse.class));
    }

    @When("Update passenger with id {int}")
    public void updatePassengerWithId(int id) {
        actual = given()
            .contentType(ContentType.JSON)
            .body(passengerRequest)
            .when()
            .put(E2ETestDataConstants.BASE_URL + E2ETestDataConstants.ID_POSTFIX, id);
    }

    @And("the response body should contain the information about updated passenger")
    public void responseBodyShouldContainTheInformationAboutUpdatedPassenger(String passengerResponseJson)
        throws Exception {
        assertThat(actual.as(PassengerResponse.class))
            .isEqualTo(objectMapper.readValue(passengerResponseJson, PassengerResponse.class));
    }

    @When("Delete passenger with id {int}")
    public void deletePassengerWithId(int id) {
        actual = given()
            .when()
            .delete(E2ETestDataConstants.BASE_URL + E2ETestDataConstants.ID_POSTFIX, id);
    }

    @Given("There is the following invalid passenger details")
    public void thereIsTheFollowingInvalidPassengerDetails(String passengerRequestJson) throws Exception {
        passengerRequest = objectMapper.readValue(passengerRequestJson, PassengerRequest.class);
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
