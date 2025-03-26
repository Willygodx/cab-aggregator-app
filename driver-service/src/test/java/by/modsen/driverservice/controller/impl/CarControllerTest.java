package by.modsen.driverservice.controller.impl;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import by.modsen.driverservice.configuration.TestConfig;
import by.modsen.driverservice.constants.CarExceptionMessageKeys;
import by.modsen.driverservice.constants.TestDataConstants;
import by.modsen.driverservice.dto.request.CarRequest;
import by.modsen.driverservice.dto.response.PageResponse;
import by.modsen.driverservice.dto.response.CarResponse;
import by.modsen.driverservice.exception.car.CarNotFoundException;
import by.modsen.driverservice.exception.car.CarNumberAlreadyExistsException;
import by.modsen.driverservice.service.CarService;
import by.modsen.driverservice.utils.LocaleUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@WebMvcTest(CarController.class)
@Import(TestConfig.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CarControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CarService carService;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private ResourcePatternResolver resourcePatternResolver;

    private Set<Locale> supportedLocales;

    @BeforeAll
    void initSupportedLocales() throws IOException {
        LocaleUtils localeUtils = new LocaleUtils(resourcePatternResolver);
        supportedLocales = localeUtils.getSupportedLocales();
    }

    Stream<String> supportedLocales() {
        return supportedLocales.stream()
            .map(Locale::toLanguageTag);
    }

    private MockHttpServletRequestBuilder withLocale(MockHttpServletRequestBuilder requestBuilder, Locale locale) {
        return requestBuilder.header("Accept-Language", locale.toLanguageTag());
    }

    @Test
    void getAllCars_ReturnsPageCarDto_ValidRequest() throws Exception {
        PageResponse<CarResponse> response = new PageResponse<>(
            TestDataConstants.PAGE_OFFSET,
            TestDataConstants.PAGE_LIMIT,
            1,
            1L,
            "sort",
            List.of(TestDataConstants.CAR_RESPONSE)
        );
        when(carService.getAllCars(anyInt(), anyInt()))
            .thenReturn(response);

        mockMvc.perform(get(TestDataConstants.GET_ALL_CARS_ENDPOINT))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(content().json(objectMapper.writeValueAsString(response)));
    }

    @Test
    void getAllCars_ReturnsBadRequest_WhenInvalidData() throws Exception {
        mockMvc.perform(get(TestDataConstants.INVALID_ARGS_GET_ALL_CARS_ENDPOINT))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.errors[0].fieldName").value("offset"))
            .andExpect(jsonPath("$.errors[0].message").value(TestDataConstants.INVALID_OFFSET_MESSAGE));
    }

    @Test
    void getCarById_ReturnsCarDto_ValidRequest() throws Exception {
        CarResponse carResponse = TestDataConstants.CAR_RESPONSE;
        Long carId = TestDataConstants.CAR_ID;

        when(carService.getCarById(anyLong()))
            .thenReturn(carResponse);

        mockMvc.perform(get(TestDataConstants.GET_CAR_BY_ID_ENDPOINT, carId))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(content().json(objectMapper.writeValueAsString(carResponse)));
    }

    @ParameterizedTest
    @MethodSource("supportedLocales")
    void getCarById_ReturnsNotFound_WhenCarDoesNotExist(String locale) throws Exception {
        Locale currentLocale = Locale.forLanguageTag(locale);
        Long carId = TestDataConstants.CAR_ID;
        String messageKey = CarExceptionMessageKeys.CAR_NOT_FOUND_MESSAGE_KEY;
        Object[] args = new Object[]{carId};
        String message = messageSource.getMessage(messageKey, args, currentLocale);

        when(carService.getCarById(carId))
            .thenThrow(new CarNotFoundException(messageKey, args));

        mockMvc.perform(withLocale(get(TestDataConstants.GET_CAR_BY_ID_ENDPOINT, carId), currentLocale))
            .andExpect(status().isNotFound())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.message").value(message))
            .andExpect(jsonPath("$.status").value(TestDataConstants.HTTP_STATUS_NOT_FOUND))
            .andExpect(jsonPath("$.timestamp").exists());

        System.out.printf((TestDataConstants.LOG_LOCALE_MESSAGE), currentLocale, message);
        verify(carService).getCarById(carId);
    }

    @Test
    void createCar_ReturnsCreatedCarDto_ValidRequest() throws Exception {
        CarRequest carRequest = TestDataConstants.CAR_REQUEST;
        CarResponse carResponse = TestDataConstants.CAR_RESPONSE;

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
            .post(TestDataConstants.CREATE_CAR_ENDPOINT)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(carRequest));
        when(carService.createCar(carRequest))
            .thenReturn(carResponse);

        mockMvc.perform(requestBuilder)
            .andExpect(status().isCreated())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(content().json(objectMapper.writeValueAsString(carResponse)));
    }

    @ParameterizedTest
    @MethodSource("supportedLocales")
    void createCar_ReturnsConflict_WhenCarAlreadyExistsByNumber(String locale) throws Exception {
        Locale currentLocale = Locale.forLanguageTag(locale);
        CarRequest carRequest = TestDataConstants.CAR_REQUEST;
        String carNumber = carRequest.carNumber();
        String messageKey = CarExceptionMessageKeys.CAR_ALREADY_EXISTS_MESSAGE_KEY;
        Object[] args = new Object[]{carNumber};
        String message = messageSource.getMessage(messageKey, args, currentLocale);

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
            .post(TestDataConstants.CREATE_CAR_ENDPOINT)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(carRequest));
        when(carService.createCar(carRequest))
            .thenThrow(new CarNumberAlreadyExistsException(messageKey, args));

        mockMvc.perform(withLocale(requestBuilder, currentLocale))
            .andExpect(status().isConflict())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.message").value(message))
            .andExpect(jsonPath("$.status").value(TestDataConstants.HTTP_STATUS_CONFLICT))
            .andExpect(jsonPath("$.timestamp").exists());

        System.out.printf((TestDataConstants.LOG_LOCALE_MESSAGE), currentLocale, message);
        verify(carService).createCar(carRequest);
    }

    @Test
    void createCar_ReturnsBadRequest_WhenInvalidData() throws Exception {
        CarRequest invalidRequest = TestDataConstants.INVALID_CAR_REQUEST_DATA;

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
            .post(TestDataConstants.CREATE_CAR_ENDPOINT)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(invalidRequest));

        mockMvc.perform(requestBuilder)
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.errors[0].fieldName").value("carNumber"))
            .andExpect(jsonPath("$.errors[0].message").value(TestDataConstants.INVALID_CAR_NUMBER));
    }

    @Test
    void updateCar_ReturnsUpdatedCarDto_ValidRequest() throws Exception {
        Long carId = TestDataConstants.CAR_ID;
        CarRequest carRequest = TestDataConstants.CAR_REQUEST;
        CarResponse carResponse = TestDataConstants.CAR_RESPONSE;

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
            .put(TestDataConstants.UPDATE_CAR_BY_ID_ENDPOINT, carId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(carRequest));
        when(carService.updateCarById(carRequest, carId))
            .thenReturn(carResponse);

        mockMvc.perform(requestBuilder)
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(content().json(objectMapper.writeValueAsString(carResponse)));
    }

    @ParameterizedTest
    @MethodSource("supportedLocales")
    void updateCar_ReturnsNotFound_WhenCarDoesNotExist(String locale) throws Exception {
        Locale currentLocale = Locale.forLanguageTag(locale);
        Long carId = TestDataConstants.CAR_ID;
        CarRequest carRequest = TestDataConstants.CAR_REQUEST;
        String messageKey = CarExceptionMessageKeys.CAR_NOT_FOUND_MESSAGE_KEY;
        Object[] args = new Object[]{carId};
        String message = messageSource.getMessage(messageKey, args, currentLocale);

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
            .put(TestDataConstants.UPDATE_CAR_BY_ID_ENDPOINT, carId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(carRequest));
        when(carService.updateCarById(carRequest, carId))
            .thenThrow(new CarNotFoundException(messageKey, args));

        mockMvc.perform(withLocale(requestBuilder, currentLocale))
            .andExpect(status().isNotFound())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.message").value(message))
            .andExpect(jsonPath("$.status").value(TestDataConstants.HTTP_STATUS_NOT_FOUND))
            .andExpect(jsonPath("$.timestamp").exists());

        System.out.printf((TestDataConstants.LOG_LOCALE_MESSAGE), currentLocale, message);
        verify(carService).updateCarById(carRequest, carId);
    }

    @ParameterizedTest
    @MethodSource("supportedLocales")
    void updateCar_ReturnsConflict_WhenCarAlreadyExistsByNumber(String locale) throws Exception {
        Locale currentLocale = Locale.forLanguageTag(locale);
        Long carId = TestDataConstants.CAR_ID;
        CarRequest carRequest = TestDataConstants.CAR_REQUEST;
        String carNumber = carRequest.carNumber();
        String messageKey = CarExceptionMessageKeys.CAR_ALREADY_EXISTS_MESSAGE_KEY;
        Object[] args = new Object[]{carNumber};
        String message = messageSource.getMessage(messageKey, args, currentLocale);

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
            .put(TestDataConstants.UPDATE_CAR_BY_ID_ENDPOINT, carId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(carRequest));
        when(carService.updateCarById(carRequest, carId))
            .thenThrow(new CarNumberAlreadyExistsException(messageKey, args));

        mockMvc.perform(withLocale(requestBuilder, currentLocale))
            .andExpect(status().isConflict())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.message").value(message))
            .andExpect(jsonPath("$.status").value(TestDataConstants.HTTP_STATUS_CONFLICT))
            .andExpect(jsonPath("$.timestamp").exists());

        System.out.printf((TestDataConstants.LOG_LOCALE_MESSAGE), currentLocale, message);
        verify(carService).updateCarById(carRequest, carId);
    }

    @Test
    void updateCar_ReturnsBadRequest_WhenInvalidData() throws Exception {
        Long carId = TestDataConstants.CAR_ID;
        CarRequest invalidRequest = TestDataConstants.INVALID_CAR_REQUEST_DATA;

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
            .put(TestDataConstants.UPDATE_CAR_BY_ID_ENDPOINT, carId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(invalidRequest));

        mockMvc.perform(requestBuilder)
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.errors[0].fieldName").value("carNumber"))
            .andExpect(jsonPath("$.errors[0].message").value(TestDataConstants.INVALID_CAR_NUMBER));
    }

    @Test
    void deleteCar_ReturnsNoContent_ValidRequest() throws Exception {
        Long carId = TestDataConstants.CAR_ID;

        MockHttpServletRequestBuilder requestBuilder = delete(
            TestDataConstants.DELETE_CAR_BY_ID_ENDPOINT,
            carId
        );

        mockMvc.perform(requestBuilder)
            .andExpect(status().isNoContent());
    }

    @ParameterizedTest
    @MethodSource("supportedLocales")
    void deleteCar_ReturnsNotFound_WhenCarDoesNotExist(String locale) throws Exception {
        Locale currentLocale = Locale.forLanguageTag(locale);
        Long carId = TestDataConstants.CAR_ID;
        String messageKey = CarExceptionMessageKeys.CAR_NOT_FOUND_MESSAGE_KEY;
        Object[] args = new Object[]{carId};
        String message = messageSource.getMessage(messageKey, args, currentLocale);

        doThrow(new CarNotFoundException(messageKey, args))
            .when(carService).deleteCarById(carId);

        mockMvc.perform(withLocale(delete(TestDataConstants.DELETE_CAR_BY_ID_ENDPOINT, carId), currentLocale))
            .andExpect(status().isNotFound())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.message").value(message))
            .andExpect(jsonPath("$.status").value(TestDataConstants.HTTP_STATUS_NOT_FOUND))
            .andExpect(jsonPath("$.timestamp").exists());

        System.out.printf((TestDataConstants.LOG_LOCALE_MESSAGE), currentLocale, message);
        verify(carService).deleteCarById(carId);
    }

}