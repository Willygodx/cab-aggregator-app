package by.modsen.driverservice.controller.impl;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import by.modsen.driverservice.constants.CarExceptionMessageKeys;
import by.modsen.driverservice.constants.TestDataConstants;
import by.modsen.driverservice.configuration.TestConfig;
import by.modsen.driverservice.constants.DriverExceptionMessageKeys;
import by.modsen.driverservice.dto.request.DriverRequest;
import by.modsen.driverservice.dto.response.DriverResponse;
import by.modsen.driverservice.dto.response.PageResponse;
import by.modsen.driverservice.exception.car.CarNotFoundException;
import by.modsen.driverservice.exception.driver.DriverAlreadyExistsException;
import by.modsen.driverservice.exception.driver.DriverNotFoundException;
import by.modsen.driverservice.service.DriverService;
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

@WebMvcTest(DriverController.class)
@Import(TestConfig.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class DriverControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private DriverService driverService;

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
    void getAllDrivers_ReturnsPageDriverResponse_ValidRequest() throws Exception {
        PageResponse<DriverResponse> response = new PageResponse<>(
            TestDataConstants.PAGE_OFFSET,
            TestDataConstants.PAGE_LIMIT,
            1,
            1L,
            "sort",
            List.of(TestDataConstants.DRIVER_RESPONSE)
        );
        when(driverService.getAllDrivers(anyInt(), anyInt()))
            .thenReturn(response);

        mockMvc.perform(get(TestDataConstants.GET_ALL_DRIVERS_ENDPOINT))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(content().json(objectMapper.writeValueAsString(response)));
    }

    @Test
    void getAllDrivers_ReturnsBadRequest_WhenInvalidData() throws Exception {
        mockMvc.perform(get(TestDataConstants.INVALID_ARGS_GET_ALL_DRIVERS_ENDPOINT))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.errors[0].fieldName").value("offset"))
            .andExpect(jsonPath("$.errors[0].message").value(TestDataConstants.INVALID_OFFSET_MESSAGE));
    }

    @Test
    void getDriverById_ReturnsDriverResponse_ValidRequest() throws Exception {
        DriverResponse driverResponse = TestDataConstants.DRIVER_RESPONSE;
        Long driverId = TestDataConstants.DRIVER_ID;

        when(driverService.getDriverById(anyLong()))
            .thenReturn(driverResponse);

        mockMvc.perform(get(TestDataConstants.GET_DRIVER_BY_ID_ENDPOINT, driverId))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(content().json(objectMapper.writeValueAsString(driverResponse)));
    }

    @ParameterizedTest
    @MethodSource("supportedLocales")
    void getDriverById_ReturnsNotFound_WhenDriverDoesNotExist(String locale) throws Exception {
        Locale currentLocale = Locale.forLanguageTag(locale);
        Long driverId = TestDataConstants.DRIVER_ID;
        String messageKey = DriverExceptionMessageKeys.DRIVER_NOT_FOUND_MESSAGE_KEY;
        Object[] args = new Object[]{driverId};
        String message = messageSource.getMessage(messageKey, args, currentLocale);

        when(driverService.getDriverById(driverId))
            .thenThrow(new DriverNotFoundException(messageKey, args));

        mockMvc.perform(withLocale(get(TestDataConstants.GET_DRIVER_BY_ID_ENDPOINT, driverId), currentLocale))
            .andExpect(status().isNotFound())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.message").value(message))
            .andExpect(jsonPath("$.status").value(TestDataConstants.HTTP_STATUS_NOT_FOUND))
            .andExpect(jsonPath("$.timestamp").exists());

        verify(driverService).getDriverById(driverId);
    }


    @Test
    void createDriver_ReturnsCreatedDriverResponse_ValidRequest() throws Exception {
        DriverRequest driverRequest = TestDataConstants.DRIVER_REQUEST;
        DriverResponse driverResponse = TestDataConstants.DRIVER_RESPONSE;

        MockHttpServletRequestBuilder requestBuilder = post(TestDataConstants.CREATE_DRIVER_ENDPOINT)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(driverRequest));
        when(driverService.createDriver(driverRequest))
            .thenReturn(driverResponse);

        mockMvc.perform(requestBuilder)
            .andExpect(status().isCreated())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(content().json(objectMapper.writeValueAsString(driverResponse)));
    }

    @ParameterizedTest
    @MethodSource("supportedLocales")
    void createDriver_ReturnsConflict_WhenDriverAlreadyExistsByEmail(String locale) throws Exception {
        Locale currentLocale = Locale.forLanguageTag(locale);
        DriverRequest driverRequest = TestDataConstants.DRIVER_REQUEST;
        String email = driverRequest.email();
        String messageKey = DriverExceptionMessageKeys.DRIVER_ALREADY_EXISTS_BY_EMAIL_MESSAGE_KEY;
        Object[] args = new Object[]{email};
        String message = messageSource.getMessage(messageKey, args, currentLocale);

        MockHttpServletRequestBuilder requestBuilder = post(TestDataConstants.CREATE_DRIVER_ENDPOINT)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(driverRequest));
        when(driverService.createDriver(driverRequest))
            .thenThrow(new DriverAlreadyExistsException(messageKey, args));

        mockMvc.perform(withLocale(requestBuilder, currentLocale))
            .andExpect(status().isConflict())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.message").value(message))
            .andExpect(jsonPath("$.status").value(TestDataConstants.HTTP_STATUS_CONFLICT))
            .andExpect(jsonPath("$.timestamp").exists());

        verify(driverService).createDriver(driverRequest);
    }

    @ParameterizedTest
    @MethodSource("supportedLocales")
    void createDriver_ReturnsConflict_WhenDriverAlreadyExistsByPhoneNumber(String locale) throws Exception {
        Locale currentLocale = Locale.forLanguageTag(locale);
        DriverRequest driverRequest = TestDataConstants.DRIVER_REQUEST;
        String phoneNumber = driverRequest.phoneNumber();
        String messageKey = DriverExceptionMessageKeys.DRIVER_ALREADY_EXISTS_BY_PHONE_KEY;
        Object[] args = new Object[]{phoneNumber};
        String message = messageSource.getMessage(messageKey, args, currentLocale);

        MockHttpServletRequestBuilder requestBuilder = post(TestDataConstants.CREATE_DRIVER_ENDPOINT)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(driverRequest));
        when(driverService.createDriver(driverRequest))
            .thenThrow(new DriverAlreadyExistsException(messageKey, args));

        mockMvc.perform(withLocale(requestBuilder, currentLocale))
            .andExpect(status().isConflict())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.message").value(message))
            .andExpect(jsonPath("$.status").value(TestDataConstants.HTTP_STATUS_CONFLICT))
            .andExpect(jsonPath("$.timestamp").exists());

        verify(driverService).createDriver(driverRequest);
    }

    @Test
    void createDriver_ReturnsBadRequest_WhenInvalidData() throws Exception {
        DriverRequest invalidRequest = TestDataConstants.INVALID_DRIVER_REQUEST_DATA;

        MockHttpServletRequestBuilder requestBuilder = post(TestDataConstants.CREATE_DRIVER_ENDPOINT)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(invalidRequest));

        mockMvc.perform(requestBuilder)
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.errors[0].fieldName").value("email"))
            .andExpect(jsonPath("$.errors[0].message").value(TestDataConstants.INVALID_EMAIL));
    }

    @Test
    void updateDriver_ReturnsUpdatedDriverResponse_ValidRequest() throws Exception {
        Long driverId = TestDataConstants.DRIVER_ID;
        DriverRequest driverRequest = TestDataConstants.DRIVER_REQUEST;
        DriverResponse driverResponse = TestDataConstants.DRIVER_RESPONSE;

        MockHttpServletRequestBuilder requestBuilder = put(TestDataConstants.UPDATE_DRIVER_BY_ID_ENDPOINT, driverId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(driverRequest));
        when(driverService.updateDriverById(driverRequest, driverId))
            .thenReturn(driverResponse);

        mockMvc.perform(requestBuilder)
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(content().json(objectMapper.writeValueAsString(driverResponse)));
    }

    @ParameterizedTest
    @MethodSource("supportedLocales")
    void updateDriver_ReturnsNotFound_WhenDriverDoesNotExist(String locale) throws Exception {
        Locale currentLocale = Locale.forLanguageTag(locale);
        Long driverId = TestDataConstants.DRIVER_ID;
        DriverRequest driverRequest = TestDataConstants.DRIVER_REQUEST;
        String messageKey = DriverExceptionMessageKeys.DRIVER_NOT_FOUND_MESSAGE_KEY;
        Object[] args = new Object[]{driverId};
        String message = messageSource.getMessage(messageKey, args, currentLocale);

        MockHttpServletRequestBuilder requestBuilder = put(TestDataConstants.UPDATE_DRIVER_BY_ID_ENDPOINT, driverId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(driverRequest));
        when(driverService.updateDriverById(driverRequest, driverId))
            .thenThrow(new DriverNotFoundException(messageKey, args));

        mockMvc.perform(withLocale(requestBuilder, currentLocale))
            .andExpect(status().isNotFound())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.message").value(message))
            .andExpect(jsonPath("$.status").value(TestDataConstants.HTTP_STATUS_NOT_FOUND))
            .andExpect(jsonPath("$.timestamp").exists());

        verify(driverService).updateDriverById(driverRequest, driverId);
    }

    @ParameterizedTest
    @MethodSource("supportedLocales")
    void updateDriver_ReturnsConflict_WhenDriverAlreadyExistsByEmail(String locale) throws Exception {
        Locale currentLocale = Locale.forLanguageTag(locale);
        Long driverId = TestDataConstants.DRIVER_ID;
        DriverRequest driverRequest = TestDataConstants.DRIVER_REQUEST;
        String email = driverRequest.email();
        String messageKey = DriverExceptionMessageKeys.DRIVER_ALREADY_EXISTS_BY_EMAIL_MESSAGE_KEY;
        Object[] args = new Object[]{email};
        String message = messageSource.getMessage(messageKey, args, currentLocale);

        MockHttpServletRequestBuilder requestBuilder = put(TestDataConstants.UPDATE_DRIVER_BY_ID_ENDPOINT, driverId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(driverRequest));
        when(driverService.updateDriverById(driverRequest, driverId))
            .thenThrow(new DriverAlreadyExistsException(messageKey, args));

        mockMvc.perform(withLocale(requestBuilder, currentLocale))
            .andExpect(status().isConflict())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.message").value(message))
            .andExpect(jsonPath("$.status").value(TestDataConstants.HTTP_STATUS_CONFLICT))
            .andExpect(jsonPath("$.timestamp").exists());

        verify(driverService).updateDriverById(driverRequest, driverId);
    }

    @ParameterizedTest
    @MethodSource("supportedLocales")
    void updateDriver_ReturnsConflict_WhenDriverAlreadyExistsByPhoneNumber(String locale) throws Exception {
        Locale currentLocale = Locale.forLanguageTag(locale);
        Long driverId = TestDataConstants.DRIVER_ID;
        DriverRequest driverRequest = TestDataConstants.DRIVER_REQUEST;
        String phoneNumber = driverRequest.phoneNumber();
        String messageKey = DriverExceptionMessageKeys.DRIVER_ALREADY_EXISTS_BY_PHONE_KEY;
        Object[] args = new Object[]{phoneNumber};
        String message = messageSource.getMessage(messageKey, args, currentLocale);

        MockHttpServletRequestBuilder requestBuilder = put(TestDataConstants.UPDATE_DRIVER_BY_ID_ENDPOINT, driverId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(driverRequest));
        when(driverService.updateDriverById(driverRequest, driverId))
            .thenThrow(new DriverAlreadyExistsException(messageKey, args));

        mockMvc.perform(withLocale(requestBuilder, currentLocale))
            .andExpect(status().isConflict())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.message").value(message))
            .andExpect(jsonPath("$.status").value(TestDataConstants.HTTP_STATUS_CONFLICT))
            .andExpect(jsonPath("$.timestamp").exists());

        verify(driverService).updateDriverById(driverRequest, driverId);
    }

    @Test
    void updateDriver_ReturnsBadRequest_WhenInvalidData() throws Exception {
        Long driverId = TestDataConstants.DRIVER_ID;
        DriverRequest invalidRequest = TestDataConstants.INVALID_DRIVER_REQUEST_DATA;

        MockHttpServletRequestBuilder requestBuilder = put(TestDataConstants.UPDATE_DRIVER_BY_ID_ENDPOINT, driverId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(invalidRequest));

        mockMvc.perform(requestBuilder)
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.errors[0].fieldName").value("email"))
            .andExpect(jsonPath("$.errors[0].message").value(TestDataConstants.INVALID_EMAIL));
    }

    @Test
    void deleteDriver_ReturnsNoContent_ValidRequest() throws Exception {
        Long driverId = TestDataConstants.DRIVER_ID;

        MockHttpServletRequestBuilder requestBuilder = delete(
            TestDataConstants.DELETE_DRIVER_BY_ID_ENDPOINT,
            driverId
        );

        mockMvc.perform(requestBuilder)
            .andExpect(status().isNoContent());
    }

    @ParameterizedTest
    @MethodSource("supportedLocales")
    void deleteDriver_ReturnsNotFound_WhenDriverDoesNotExist(String locale) throws Exception {
        Locale currentLocale = Locale.forLanguageTag(locale);
        Long driverId = TestDataConstants.DRIVER_ID;
        String messageKey = DriverExceptionMessageKeys.DRIVER_NOT_FOUND_MESSAGE_KEY;
        Object[] args = new Object[]{driverId};
        String message = messageSource.getMessage(messageKey, args, currentLocale);

        doThrow(new DriverNotFoundException(messageKey, args))
            .when(driverService).deleteDriverById(driverId);

        mockMvc.perform(withLocale(delete(TestDataConstants.DELETE_DRIVER_BY_ID_ENDPOINT, driverId), currentLocale))
            .andExpect(status().isNotFound())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.message").value(message))
            .andExpect(jsonPath("$.status").value(TestDataConstants.HTTP_STATUS_NOT_FOUND))
            .andExpect(jsonPath("$.timestamp").exists());

        verify(driverService).deleteDriverById(driverId);
    }

    @Test
    void addCarToDriver_ReturnsOk_ValidRequest() throws Exception {
        Long driverId = TestDataConstants.DRIVER_ID;
        Long carId = TestDataConstants.CAR_ID;

        MockHttpServletRequestBuilder requestBuilder = post(
            TestDataConstants.ADD_CAR_TO_DRIVER_ENDPOINT,
            driverId,
            carId
        );

        mockMvc.perform(requestBuilder)
            .andExpect(status().isOk());
    }

    @ParameterizedTest
    @MethodSource("supportedLocales")
    void addCarToDriver_ReturnsNotFound_WhenDriverDoesNotExist(String locale) throws Exception {
        Locale currentLocale = Locale.forLanguageTag(locale);
        Long driverId = TestDataConstants.DRIVER_ID;
        Long carId = TestDataConstants.CAR_ID;
        String messageKey = DriverExceptionMessageKeys.DRIVER_NOT_FOUND_MESSAGE_KEY;
        Object[] args = new Object[]{driverId};
        String message = messageSource.getMessage(messageKey, args, currentLocale);

        doThrow(new DriverNotFoundException(messageKey, args))
            .when(driverService).addCarToDriver(driverId, carId);

        mockMvc.perform(withLocale(post(TestDataConstants.ADD_CAR_TO_DRIVER_ENDPOINT, driverId, carId), currentLocale))
            .andExpect(status().isNotFound())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.message").value(message))
            .andExpect(jsonPath("$.status").value(TestDataConstants.HTTP_STATUS_NOT_FOUND))
            .andExpect(jsonPath("$.timestamp").exists());

        verify(driverService).addCarToDriver(driverId, carId);
    }

    @ParameterizedTest
    @MethodSource("supportedLocales")
    void addCarToDriver_ReturnsNotFound_WhenCarDoesNotExist(String locale) throws Exception {
        Locale currentLocale = Locale.forLanguageTag(locale);
        Long driverId = TestDataConstants.DRIVER_ID;
        Long carId = TestDataConstants.CAR_ID;
        String messageKey = CarExceptionMessageKeys.CAR_NOT_FOUND_MESSAGE_KEY;
        Object[] args = new Object[]{carId};
        String message = messageSource.getMessage(messageKey, args, currentLocale);

        doThrow(new CarNotFoundException(messageKey, args))
            .when(driverService).addCarToDriver(driverId, carId);

        mockMvc.perform(withLocale(post(TestDataConstants.ADD_CAR_TO_DRIVER_ENDPOINT, driverId, carId), currentLocale))
            .andExpect(status().isNotFound())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.message").value(message))
            .andExpect(jsonPath("$.status").value(TestDataConstants.HTTP_STATUS_NOT_FOUND))
            .andExpect(jsonPath("$.timestamp").exists());

        verify(driverService).addCarToDriver(driverId, carId);
    }

}