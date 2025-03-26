package by.modsen.passengerservice.controller.impl;

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

import by.modsen.passengerservice.configuration.TestConfig;
import by.modsen.passengerservice.constants.PassengerExceptionMessageKeys;
import by.modsen.passengerservice.constants.TestDataConstants;
import by.modsen.passengerservice.dto.request.PassengerRequest;
import by.modsen.passengerservice.dto.response.PageResponse;
import by.modsen.passengerservice.dto.response.PassengerResponse;
import by.modsen.passengerservice.exception.passenger.PassengerAlreadyExistsException;
import by.modsen.passengerservice.exception.passenger.PassengerNotFoundException;
import by.modsen.passengerservice.service.PassengerService;
import by.modsen.passengerservice.utils.LocaleUtils;
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

@WebMvcTest(PassengerController.class)
@Import(TestConfig.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PassengerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PassengerService passengerService;

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
    void getAllPassengers_ReturnsPagePassengerDto_ValidRequest() throws Exception {
        PageResponse<PassengerResponse> response = new PageResponse<>(
            TestDataConstants.PAGE_OFFSET,
            TestDataConstants.PAGE_LIMIT,
            1,
            1L,
            "sort",
            List.of(TestDataConstants.PASSENGER_RESPONSE)
        );
        when(passengerService.getAllPassengers(anyInt(), anyInt()))
            .thenReturn(response);

        mockMvc.perform(get(TestDataConstants.GET_ALL_PASSENGERS_ENDPOINT))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(content().json(objectMapper.writeValueAsString(response)));
    }

    @Test
    void getAllPassengers_ReturnsBadRequest_WhenInvalidData() throws Exception {
        mockMvc.perform(get(TestDataConstants.INVALID_ARGS_GET_ALL_PASSENGERS_ENDPOINT))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.errors[0].fieldName").value("offset"))
            .andExpect(jsonPath("$.errors[0].message").value(TestDataConstants.INVALID_OFFSET_MESSAGE));
    }

    @Test
    void getPassengerById_ReturnsPassengerDto_ValidRequest() throws Exception {
        PassengerResponse passengerResponse = TestDataConstants.PASSENGER_RESPONSE;
        Long passengerId = TestDataConstants.PASSENGER_ID;

        when(passengerService.getPassengerById(anyLong()))
            .thenReturn(passengerResponse);

        mockMvc.perform(get(TestDataConstants.GET_PASSENGER_BY_ID_ENDPOINT, passengerId))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(content().json(objectMapper.writeValueAsString(passengerResponse)));
    }

    @ParameterizedTest
    @MethodSource("supportedLocales")
    void getPassengerById_ReturnsNotFound_WhenPassengerDoesNotExist(String locale) throws Exception {
        Locale currentLocale = Locale.forLanguageTag(locale);
        Long passengerId = TestDataConstants.PASSENGER_ID;
        String messageKey = PassengerExceptionMessageKeys.PASSENGER_NOT_FOUND_MESSAGE_KEY;
        Object[] args = new Object[]{passengerId};
        String message = messageSource.getMessage(messageKey, args, currentLocale);

        when(passengerService.getPassengerById(passengerId))
            .thenThrow(new PassengerNotFoundException(messageKey, args));

        mockMvc.perform(withLocale(get(TestDataConstants.GET_PASSENGER_BY_ID_ENDPOINT, passengerId), currentLocale))
            .andExpect(status().isNotFound())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.message").value(message))
            .andExpect(jsonPath("$.status").value(TestDataConstants.HTTP_STATUS_NOT_FOUND))
            .andExpect(jsonPath("$.timestamp").exists());

        System.out.printf((TestDataConstants.LOG_LOCALE_MESSAGE), currentLocale, message);
        verify(passengerService).getPassengerById(passengerId);
    }

    @Test
    void createPassenger_ReturnsCreatedPassengerDto_ValidRequest() throws Exception {
        PassengerRequest passengerRequest = TestDataConstants.PASSENGER_REQUEST;
        PassengerResponse passengerResponse = TestDataConstants.PASSENGER_RESPONSE;

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
            .post(TestDataConstants.CREATE_PASSENGER_ENDPOINT)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(passengerRequest));
        when(passengerService.createPassenger(passengerRequest))
            .thenReturn(passengerResponse);

        mockMvc.perform(requestBuilder)
            .andExpect(status().isCreated())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(content().json(objectMapper.writeValueAsString(passengerResponse)));
    }

    @ParameterizedTest
    @MethodSource("supportedLocales")
    void createPassenger_ReturnsConflict_WhenPassengerAlreadyExistsByEmail(String locale) throws Exception {
        Locale currentLocale = Locale.forLanguageTag(locale);
        PassengerRequest passengerRequest = TestDataConstants.PASSENGER_REQUEST;
        String email = passengerRequest.email();
        String messageKey = PassengerExceptionMessageKeys.PASSENGER_EMAIL_ALREADY_EXISTS_MESSAGE_KEY;
        Object[] args = new Object[]{email};
        String message = messageSource.getMessage(messageKey, args, currentLocale);

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
            .post(TestDataConstants.CREATE_PASSENGER_ENDPOINT)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(passengerRequest));
        when(passengerService.createPassenger(passengerRequest))
            .thenThrow(new PassengerAlreadyExistsException(messageKey, args));

        mockMvc.perform(withLocale(requestBuilder, currentLocale))
            .andExpect(status().isConflict())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.message").value(message))
            .andExpect(jsonPath("$.status").value(TestDataConstants.HTTP_STATUS_CONFLICT))
            .andExpect(jsonPath("$.timestamp").exists());

        System.out.printf((TestDataConstants.LOG_LOCALE_MESSAGE), currentLocale, message);
        verify(passengerService).createPassenger(passengerRequest);
    }

    @ParameterizedTest
    @MethodSource("supportedLocales")
    void createPassenger_ReturnsConflict_WhenPassengerAlreadyExistsByPhoneNumber(String locale) throws Exception {
        Locale currentLocale = Locale.forLanguageTag(locale);
        PassengerRequest passengerRequest = TestDataConstants.PASSENGER_REQUEST;
        String phoneNumber = passengerRequest.phoneNumber();
        String messageKey = PassengerExceptionMessageKeys.PASSENGER_PHONE_NUMBER_ALREADY_EXISTS_MESSAGE_KEY;
        Object[] args = new Object[]{phoneNumber};
        String message = messageSource.getMessage(messageKey, args, currentLocale);

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
            .post(TestDataConstants.CREATE_PASSENGER_ENDPOINT)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(passengerRequest));
        when(passengerService.createPassenger(passengerRequest))
            .thenThrow(new PassengerAlreadyExistsException(messageKey, args));

        mockMvc.perform(withLocale(requestBuilder, currentLocale))
            .andExpect(status().isConflict())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.message").value(message))
            .andExpect(jsonPath("$.status").value(TestDataConstants.HTTP_STATUS_CONFLICT))
            .andExpect(jsonPath("$.timestamp").exists());

        System.out.printf((TestDataConstants.LOG_LOCALE_MESSAGE), currentLocale, message);
        verify(passengerService).createPassenger(passengerRequest);
    }

    @Test
    void createPassenger_ReturnsBadRequest_WhenInvalidData() throws Exception {
        PassengerRequest invalidRequest = TestDataConstants.INVALID_PASSENGER_REQUEST_DATA;

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
            .post(TestDataConstants.CREATE_PASSENGER_ENDPOINT)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(invalidRequest));

        mockMvc.perform(requestBuilder)
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.errors[0].fieldName").value("email"))
            .andExpect(jsonPath("$.errors[0].message").value(TestDataConstants.INVALID_EMAIL));
    }

    @Test
    void updatePassenger_ReturnsUpdatedPassengerDto_ValidRequest() throws Exception {
        Long passengerId = TestDataConstants.PASSENGER_ID;
        PassengerRequest passengerRequest = TestDataConstants.PASSENGER_REQUEST;
        PassengerResponse passengerResponse = TestDataConstants.PASSENGER_RESPONSE;

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
            .put(TestDataConstants.UPDATE_PASSENGER_BY_ID_ENDPOINT, passengerId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(passengerRequest));
        when(passengerService.updatePassengerById(passengerRequest, passengerId))
            .thenReturn(passengerResponse);

        mockMvc.perform(requestBuilder)
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(content().json(objectMapper.writeValueAsString(passengerResponse)));
    }

    @ParameterizedTest
    @MethodSource("supportedLocales")
    void updatePassenger_ReturnsNotFound_WhenPassengerDoesNotExist(String locale) throws Exception {
        Locale currentLocale = Locale.forLanguageTag(locale);
        Long passengerId = TestDataConstants.PASSENGER_ID;
        PassengerRequest passengerRequest = TestDataConstants.PASSENGER_REQUEST;
        String messageKey = PassengerExceptionMessageKeys.PASSENGER_NOT_FOUND_MESSAGE_KEY;
        Object[] args = new Object[]{passengerId};
        String message = messageSource.getMessage(messageKey, args, currentLocale);

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
            .put(TestDataConstants.UPDATE_PASSENGER_BY_ID_ENDPOINT, passengerId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(passengerRequest));
        when(passengerService.updatePassengerById(passengerRequest, passengerId))
            .thenThrow(new PassengerNotFoundException(messageKey, args));

        mockMvc.perform(withLocale(requestBuilder, currentLocale))
            .andExpect(status().isNotFound())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.message").value(message))
            .andExpect(jsonPath("$.status").value(TestDataConstants.HTTP_STATUS_NOT_FOUND))
            .andExpect(jsonPath("$.timestamp").exists());

        System.out.printf((TestDataConstants.LOG_LOCALE_MESSAGE), currentLocale, message);
        verify(passengerService).updatePassengerById(passengerRequest, passengerId);
    }

    @ParameterizedTest
    @MethodSource("supportedLocales")
    void updatePassenger_ReturnsConflict_WhenPassengerAlreadyExistsByEmail(String locale) throws Exception {
        Locale currentLocale = Locale.forLanguageTag(locale);
        Long passengerId = TestDataConstants.PASSENGER_ID;
        PassengerRequest passengerRequest = TestDataConstants.PASSENGER_REQUEST;
        String email = passengerRequest.email();
        String messageKey = PassengerExceptionMessageKeys.PASSENGER_EMAIL_ALREADY_EXISTS_MESSAGE_KEY;
        Object[] args = new Object[]{email};
        String message = messageSource.getMessage(messageKey, args, currentLocale);

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
            .put(TestDataConstants.UPDATE_PASSENGER_BY_ID_ENDPOINT, passengerId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(passengerRequest));
        when(passengerService.updatePassengerById(passengerRequest, passengerId))
            .thenThrow(new PassengerAlreadyExistsException(messageKey, args));

        mockMvc.perform(withLocale(requestBuilder, currentLocale))
            .andExpect(status().isConflict())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.message").value(message))
            .andExpect(jsonPath("$.status").value(TestDataConstants.HTTP_STATUS_CONFLICT))
            .andExpect(jsonPath("$.timestamp").exists());

        System.out.printf((TestDataConstants.LOG_LOCALE_MESSAGE), currentLocale, message);
        verify(passengerService).updatePassengerById(passengerRequest, passengerId);
    }

    @ParameterizedTest
    @MethodSource("supportedLocales")
    void updatePassenger_ReturnsConflict_WhenPassengerAlreadyExistsByPhoneNumber(String locale) throws Exception {
        Locale currentLocale = Locale.forLanguageTag(locale);
        Long passengerId = TestDataConstants.PASSENGER_ID;
        PassengerRequest passengerRequest = TestDataConstants.PASSENGER_REQUEST;
        String phoneNumber = passengerRequest.phoneNumber();
        String messageKey = PassengerExceptionMessageKeys.PASSENGER_PHONE_NUMBER_ALREADY_EXISTS_MESSAGE_KEY;
        Object[] args = new Object[]{phoneNumber};
        String message = messageSource.getMessage(messageKey, args, currentLocale);

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
            .put(TestDataConstants.UPDATE_PASSENGER_BY_ID_ENDPOINT, passengerId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(passengerRequest));
        when(passengerService.updatePassengerById(passengerRequest, passengerId))
            .thenThrow(new PassengerAlreadyExistsException(messageKey, args));

        mockMvc.perform(withLocale(requestBuilder, currentLocale))
            .andExpect(status().isConflict())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.message").value(message))
            .andExpect(jsonPath("$.status").value(TestDataConstants.HTTP_STATUS_CONFLICT))
            .andExpect(jsonPath("$.timestamp").exists());

        System.out.printf((TestDataConstants.LOG_LOCALE_MESSAGE), currentLocale, message);
        verify(passengerService).updatePassengerById(passengerRequest, passengerId);
    }

    @Test
    void updatePassenger_ReturnsBadRequest_WhenInvalidData() throws Exception {
        Long passengerId = TestDataConstants.PASSENGER_ID;
        PassengerRequest invalidRequest = TestDataConstants.INVALID_PASSENGER_REQUEST_DATA;

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
            .put(TestDataConstants.UPDATE_PASSENGER_BY_ID_ENDPOINT, passengerId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(invalidRequest));

        mockMvc.perform(requestBuilder)
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.errors[0].fieldName").value("email"))
            .andExpect(jsonPath("$.errors[0].message").value(TestDataConstants.INVALID_EMAIL));
    }

    @Test
    void deletePassenger_ReturnsNoContent_ValidRequest() throws Exception {
        Long passengerId = TestDataConstants.PASSENGER_ID;

        MockHttpServletRequestBuilder requestBuilder = delete(
            TestDataConstants.DELETE_PASSENGER_BY_ID_ENDPOINT,
            passengerId
        );

        mockMvc.perform(requestBuilder)
            .andExpect(status().isNoContent());
    }

    @ParameterizedTest
    @MethodSource("supportedLocales")
    void deletePassenger_ReturnsNotFound_WhenPassengerDoesNotExist(String locale) throws Exception {
        Locale currentLocale = Locale.forLanguageTag(locale);
        Long passengerId = TestDataConstants.PASSENGER_ID;
        String messageKey = PassengerExceptionMessageKeys.PASSENGER_NOT_FOUND_MESSAGE_KEY;
        Object[] args = new Object[]{passengerId};
        String message = messageSource.getMessage(messageKey, args, currentLocale);

        doThrow(new PassengerNotFoundException(messageKey, args))
            .when(passengerService).deletePassengerById(passengerId);

        mockMvc.perform(withLocale(delete(TestDataConstants.DELETE_PASSENGER_BY_ID_ENDPOINT, passengerId), currentLocale))
            .andExpect(status().isNotFound())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.message").value(message))
            .andExpect(jsonPath("$.status").value(TestDataConstants.HTTP_STATUS_NOT_FOUND))
            .andExpect(jsonPath("$.timestamp").exists());

        System.out.printf((TestDataConstants.LOG_LOCALE_MESSAGE), currentLocale, message);
        verify(passengerService).deletePassengerById(passengerId);
    }

}
