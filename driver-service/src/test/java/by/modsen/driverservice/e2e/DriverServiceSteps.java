package by.modsen.driverservice.e2e;

import by.modsen.driverservice.constants.E2ETestDataConstants;
import by.modsen.driverservice.dto.request.CarRequest;
import by.modsen.driverservice.dto.request.DriverRequest;
import by.modsen.driverservice.dto.response.CarResponse;
import by.modsen.driverservice.dto.response.DriverResponse;
import by.modsen.driverservice.dto.response.PageResponse;
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

public class DriverServiceSteps {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private Response actual;

    private CarRequest carRequest;
    private DriverRequest driverRequest;

    @Given("There is the following car details")
    public void prepareCarDetails(String carRequestJson) throws Exception {
        carRequest = objectMapper.readValue(carRequestJson, CarRequest.class);
    }

    @Given("There is the following driver details")
    public void prepareDriverDetails(String driverRequestJson) throws Exception {
        driverRequest = objectMapper.readValue(driverRequestJson, DriverRequest.class);
    }

    @When("Creates the car")
    public void createCar() {
        actual = given()
            .contentType(ContentType.JSON)
            .body(carRequest)
            .when()
            .post(E2ETestDataConstants.BASE_CARS_URL);
    }

    @When("Creates the driver")
    public void createDriver() {
        actual = given()
            .contentType(ContentType.JSON)
            .body(driverRequest)
            .when()
            .post(E2ETestDataConstants.BASE_DRIVERS_URL);
    }

    @Then("the response status is {int}")
    public void responseStatusShouldBe(int expectedStatus) {
        actual
            .then()
            .statusCode(expectedStatus);
    }

    @And("the response body should contain the information about created car")
    public void responseBodyShouldContainTheInformationAboutCreatedCar(String carResponseJson) throws Exception {
        CarResponse expected = objectMapper.readValue(carResponseJson, CarResponse.class);
        CarResponse actualResponse = actual.as(CarResponse.class);
        assertThat(actualResponse)
            .usingRecursiveComparison()
            .ignoringCollectionOrderInFields("driverIds")
            .isEqualTo(expected);
    }

    @And("the response body should contain the information about created driver")
    public void responseBodyShouldContainTheInformationAboutCreatedDriver(String driverResponseJson) throws Exception {
        DriverResponse expected = objectMapper.readValue(driverResponseJson, DriverResponse.class);
        DriverResponse actualResponse = actual.as(DriverResponse.class);
        assertThat(actualResponse)
            .usingRecursiveComparison()
            .ignoringCollectionOrderInFields("carIds")
            .isEqualTo(expected);
    }

    @When("Get a page with cars with current offset {int} and limit {int}")
    public void getPageWithCarsWithCurrentOffsetAndLimit(int offset, int limit) {
        actual = given()
            .when()
            .get(E2ETestDataConstants.BASE_CARS_URL
                + E2ETestDataConstants.OFFSET_PARAM
                + offset
                + E2ETestDataConstants.LIMIT_PARAM
                + limit);
    }

    @When("Get a page with drivers with current offset {int} and limit {int}")
    public void getPageWithDriversWithCurrentOffsetAndLimit(int offset, int limit) {
        actual = given()
            .when()
            .get(E2ETestDataConstants.BASE_DRIVERS_URL
                + E2ETestDataConstants.OFFSET_PARAM
                + offset
                + E2ETestDataConstants.LIMIT_PARAM
                + limit);
    }

    @And("the response body should contain the information about first ten cars")
    public void responseBodyShouldContainInformationAboutFirstTenCars(String carResponseJson) throws Exception {
        PageResponse expected = objectMapper.readValue(carResponseJson, PageResponse.class);
        PageResponse actualResponse = actual.as(PageResponse.class);
        assertThat(actualResponse)
            .usingRecursiveComparison()
            .ignoringCollectionOrderInFields("values", "values.driverIds")
            .isEqualTo(expected);
    }

    @And("the response body should contain the information about first ten drivers")
    public void responseBodyShouldContainInformationAboutFirstTenDrivers(String driverResponseJson) throws Exception {
        PageResponse expected = objectMapper.readValue(driverResponseJson, PageResponse.class);
        PageResponse actualResponse = actual.as(PageResponse.class);
        assertThat(actualResponse)
            .usingRecursiveComparison()
            .ignoringCollectionOrderInFields("values", "values.carIds")
            .isEqualTo(expected);
    }

    @When("Get car with id {int}")
    public void getCarWithId(int id) {
        actual = given()
            .when()
            .get(E2ETestDataConstants.BASE_CARS_URL + E2ETestDataConstants.ID_POSTFIX, id);
    }

    @When("Get driver with id {int}")
    public void getDriverWithId(int id) {
        actual = given()
            .when()
            .get(E2ETestDataConstants.BASE_DRIVERS_URL + E2ETestDataConstants.ID_POSTFIX, id);
    }

    @And("the response body should contain the information about car")
    public void responseBodyShouldContainTheInformationAboutCar(String carResponseJson) throws Exception {
        CarResponse expected = objectMapper.readValue(carResponseJson, CarResponse.class);
        CarResponse actualResponse = actual.as(CarResponse.class);
        assertThat(actualResponse)
            .usingRecursiveComparison()
            .ignoringCollectionOrderInFields("driverIds")
            .isEqualTo(expected);
    }

    @And("the response body should contain the information about driver")
    public void responseBodyShouldContainTheInformationAboutDriver(String driverResponseJson) throws Exception {
        DriverResponse expected = objectMapper.readValue(driverResponseJson, DriverResponse.class);
        DriverResponse actualResponse = actual.as(DriverResponse.class);
        assertThat(actualResponse)
            .usingRecursiveComparison()
            .ignoringCollectionOrderInFields("carIds")
            .isEqualTo(expected);
    }

    @When("Update car with id {int}")
    public void updateCarWithId(int id) {
        actual = given()
            .contentType(ContentType.JSON)
            .body(carRequest)
            .when()
            .put(E2ETestDataConstants.BASE_CARS_URL + E2ETestDataConstants.ID_POSTFIX, id);
    }

    @When("Update driver with id {int}")
    public void updateDriverWithId(int id) {
        actual = given()
            .contentType(ContentType.JSON)
            .body(driverRequest)
            .when()
            .put(E2ETestDataConstants.BASE_DRIVERS_URL + E2ETestDataConstants.ID_POSTFIX, id);
    }

    @And("the response body should contain the information about updated car")
    public void responseBodyShouldContainTheInformationAboutUpdatedCar(String carResponseJson) throws Exception {
        CarResponse expected = objectMapper.readValue(carResponseJson, CarResponse.class);
        CarResponse actualResponse = actual.as(CarResponse.class);
        assertThat(actualResponse)
            .usingRecursiveComparison()
            .ignoringCollectionOrderInFields("driverIds")
            .isEqualTo(expected);
    }

    @And("the response body should contain the information about updated driver")
    public void responseBodyShouldContainTheInformationAboutUpdatedDriver(String driverResponseJson) throws Exception {
        DriverResponse expected = objectMapper.readValue(driverResponseJson, DriverResponse.class);
        DriverResponse actualResponse = actual.as(DriverResponse.class);
        assertThat(actualResponse)
            .usingRecursiveComparison()
            .ignoringCollectionOrderInFields("carIds")
            .isEqualTo(expected);
    }

    @When("Delete car with id {int}")
    public void deleteCarWithId(int id) {
        actual = given()
            .when()
            .delete(E2ETestDataConstants.BASE_CARS_URL + E2ETestDataConstants.ID_POSTFIX, id);
    }

    @When("Delete driver with id {int}")
    public void deleteDriverWithId(int id) {
        actual = given()
            .when()
            .delete(E2ETestDataConstants.BASE_DRIVERS_URL + E2ETestDataConstants.ID_POSTFIX, id);
    }

    @Given("There is the following invalid car details")
    public void thereIsTheFollowingInvalidCarDetails(String carRequestJson) throws Exception {
        carRequest = objectMapper.readValue(carRequestJson, CarRequest.class);
    }

    @Given("There is the following invalid driver details")
    public void thereIsTheFollowingInvalidDriverDetails(String driverRequestJson) throws Exception {
        driverRequest = objectMapper.readValue(driverRequestJson, DriverRequest.class);
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

    @Given("There is an existing driver with id {int} and an existing car with id {int}")
    public void thereIsAnExistingDriverWithIdAndAnExistingCarWithId(int driverId, int carId) {
    }

    @Given("There is an existing car with id {int}")
    public void thereIsAnExistingCarWithId(int carId) {
    }

    @Given("There is an existing driver with id {int}")
    public void thereIsAnExistingDriverWithId(int driverId) {
    }

    @When("Add car with id {int} to driver with id {int}")
    public void addCarToDriver(int carId, int driverId) {
        actual = given()
            .when()
            .post(E2ETestDataConstants.BASE_DRIVERS_URL + "/" + driverId + "/add-car/" + carId);
    }

}