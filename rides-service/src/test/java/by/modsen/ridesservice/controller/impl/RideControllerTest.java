package by.modsen.ridesservice.controller.impl;

import by.modsen.ridesservice.configuration.TestConfig;
import by.modsen.ridesservice.constants.RideExceptionMessageKeys;
import by.modsen.ridesservice.constants.TestDataConstants;
import by.modsen.ridesservice.dto.request.RideRequest;
import by.modsen.ridesservice.dto.request.RideStatusRequest;
import by.modsen.ridesservice.dto.response.PageResponse;
import by.modsen.ridesservice.dto.response.RideResponse;
import by.modsen.ridesservice.exception.ride.RideNotFoundException;
import by.modsen.ridesservice.exception.ride.RideStatusIncorrectException;
import by.modsen.ridesservice.service.RideService;
import by.modsen.ridesservice.utils.LocaleUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
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

import java.util.List;
import java.util.Locale;
import java.util.Set;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RideController.class)
@Import(TestConfig.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class RideControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private RideService rideService;

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
    void createRide_ReturnsCreatedRideResponse_ValidRequest() throws Exception {
        RideRequest rideRequest = TestDataConstants.RIDE_REQUEST;
        RideResponse rideResponse = TestDataConstants.RIDE_RESPONSE;

        MockHttpServletRequestBuilder requestBuilder = post(TestDataConstants.CREATE_RIDE_ENDPOINT)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(rideRequest));
        when(rideService.createRide(rideRequest, TestDataConstants.LANGUAGE_TAG))
            .thenReturn(rideResponse);

        mockMvc.perform(requestBuilder)
            .andExpect(status().isCreated())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(content().json(objectMapper.writeValueAsString(rideResponse)));
    }

    @ParameterizedTest
    @MethodSource("supportedLocales")
    void createRide_ReturnsNotFound_WhenDriverOrPassengerNotFound(String locale) throws Exception {
        Locale currentLocale = Locale.forLanguageTag(locale);
        RideRequest rideRequest = TestDataConstants.RIDE_REQUEST;
        String messageKey = RideExceptionMessageKeys.RIDE_NOT_FOUND_MESSAGE;
        Object[] args = new Object[]{TestDataConstants.DRIVER_ID};
        String message = messageSource.getMessage(messageKey, args, currentLocale);

        MockHttpServletRequestBuilder requestBuilder = post(TestDataConstants.CREATE_RIDE_ENDPOINT)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(rideRequest));
        when(rideService.createRide(rideRequest, currentLocale.toLanguageTag()))
            .thenThrow(new RideNotFoundException(messageKey, args));

        mockMvc.perform(withLocale(requestBuilder, currentLocale))
            .andExpect(status().isNotFound())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.message").value(message))
            .andExpect(jsonPath("$.status").value(TestDataConstants.HTTP_STATUS_NOT_FOUND))
            .andExpect(jsonPath("$.timestamp").exists());

        verify(rideService).createRide(rideRequest, currentLocale.toLanguageTag());
    }

    @Test
    void createRide_ReturnsBadRequest_WhenInvalidData() throws Exception {
        RideRequest invalidRequest = TestDataConstants.INVALID_RIDE_REQUEST_DATA;

        MockHttpServletRequestBuilder requestBuilder = post(TestDataConstants.CREATE_RIDE_ENDPOINT)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(invalidRequest));

        mockMvc.perform(requestBuilder)
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.errors[0].fieldName").value("destinationAddress"))
            .andExpect(jsonPath("$.errors[0].message").value(TestDataConstants.INVALID_DESTINATION_ADDRESS));
    }

    @Test
    void updateRide_ReturnsUpdatedRideResponse_ValidRequest() throws Exception {
        Long rideId = TestDataConstants.RIDE_ID;
        RideRequest rideRequest = TestDataConstants.RIDE_REQUEST_FOR_UPDATE;
        RideResponse rideResponse = TestDataConstants.RIDE_RESPONSE;

        MockHttpServletRequestBuilder requestBuilder = put(TestDataConstants.UPDATE_RIDE_ENDPOINT, rideId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(rideRequest));
        when(rideService.updateRide(rideRequest, rideId))
            .thenReturn(rideResponse);

        mockMvc.perform(requestBuilder)
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(content().json(objectMapper.writeValueAsString(rideResponse)));
    }

    @Test
    void updateRideStatus_ReturnsUpdatedRideResponse_ValidRequest() throws Exception {
        Long rideId = TestDataConstants.RIDE_ID;
        RideStatusRequest rideStatusRequest = TestDataConstants.RIDE_STATUS_REQUEST;
        RideResponse rideResponse = TestDataConstants.RIDE_RESPONSE;

        MockHttpServletRequestBuilder requestBuilder = patch(TestDataConstants.PATCH_RIDE_STATUS_ENDPOINT, rideId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(rideStatusRequest));
        when(rideService.updateRideStatus(rideStatusRequest, rideId))
            .thenReturn(rideResponse);

        mockMvc.perform(requestBuilder)
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(content().json(objectMapper.writeValueAsString(rideResponse)));
    }

    @ParameterizedTest
    @MethodSource("supportedLocales")
    void updateRideStatus_ReturnsNotFound_WhenRideNotFound(String locale) throws Exception {
        Locale currentLocale = Locale.forLanguageTag(locale);
        Long rideId = TestDataConstants.RIDE_ID;
        RideStatusRequest rideStatusRequest = TestDataConstants.RIDE_STATUS_REQUEST;
        String messageKey = RideExceptionMessageKeys.RIDE_NOT_FOUND_MESSAGE;
        Object[] args = new Object[]{rideId};
        String message = messageSource.getMessage(messageKey, args, currentLocale);

        MockHttpServletRequestBuilder requestBuilder = patch(TestDataConstants.PATCH_RIDE_STATUS_ENDPOINT, rideId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(rideStatusRequest));
        when(rideService.updateRideStatus(rideStatusRequest, rideId))
            .thenThrow(new RideNotFoundException(messageKey, args));

        mockMvc.perform(withLocale(requestBuilder, currentLocale))
            .andExpect(status().isNotFound())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.message").value(message))
            .andExpect(jsonPath("$.status").value(TestDataConstants.HTTP_STATUS_NOT_FOUND))
            .andExpect(jsonPath("$.timestamp").exists());

        verify(rideService).updateRideStatus(rideStatusRequest, rideId);
    }

    @ParameterizedTest
    @MethodSource("supportedLocales")
    void updateRideStatus_ReturnsConflict_WhenStatusIncorrect(String locale) throws Exception {
        Locale currentLocale = Locale.forLanguageTag(locale);
        Long rideId = TestDataConstants.RIDE_ID;
        RideStatusRequest rideStatusRequest = TestDataConstants.RIDE_STATUS_REQUEST;
        String messageKey = RideExceptionMessageKeys.RIDE_STATUS_INCORRECT_MESSAGE;
        Object[] args = new Object[]{TestDataConstants.RIDE_STATUS_CREATED, TestDataConstants.RIDE_STATUS_FINISHED};
        String message = messageSource.getMessage(messageKey, args, currentLocale);

        MockHttpServletRequestBuilder requestBuilder = patch(TestDataConstants.PATCH_RIDE_STATUS_ENDPOINT, rideId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(rideStatusRequest));
        when(rideService.updateRideStatus(rideStatusRequest, rideId))
            .thenThrow(new RideStatusIncorrectException(messageKey, args));

        mockMvc.perform(withLocale(requestBuilder, currentLocale))
            .andExpect(status().isConflict())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.message").value(message))
            .andExpect(jsonPath("$.status").value(TestDataConstants.HTTP_STATUS_CONFLICT))
            .andExpect(jsonPath("$.timestamp").exists());

        verify(rideService).updateRideStatus(rideStatusRequest, rideId);
    }

    @Test
    void getAllRides_ReturnsPageRideResponse_ValidRequest() throws Exception {
        PageResponse<RideResponse> response = new PageResponse<>(
            TestDataConstants.PAGE_OFFSET,
            TestDataConstants.PAGE_LIMIT,
            1,
            1L,
            "sort",
            List.of(TestDataConstants.RIDE_RESPONSE)
        );
        when(rideService.getAllRides(anyInt(), anyInt()))
            .thenReturn(response);

        mockMvc.perform(get(TestDataConstants.GET_ALL_RIDES_ENDPOINT))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(content().json(objectMapper.writeValueAsString(response)));
    }

    @Test
    void getAllRidesByDriver_ReturnsPageRideResponse_ValidRequest() throws Exception {
        Long driverId = TestDataConstants.DRIVER_ID;
        PageResponse<RideResponse> response = new PageResponse<>(
            TestDataConstants.PAGE_OFFSET,
            TestDataConstants.PAGE_LIMIT,
            1,
            1L,
            "sort",
            List.of(TestDataConstants.RIDE_RESPONSE)
        );
        when(rideService.getAllRidesByDriver(anyInt(), anyInt(), anyLong(), anyString()))
            .thenReturn(response);

        mockMvc.perform(get(TestDataConstants.GET_ALL_RIDES_ENDPOINT + "/drivers/{driverId}", driverId))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(content().json(objectMapper.writeValueAsString(response)));
    }

    @ParameterizedTest
    @MethodSource("supportedLocales")
    void getAllRidesByDriver_ReturnsNotFound_WhenDriverNotFound(String locale) throws Exception {
        Locale currentLocale = Locale.forLanguageTag(locale);
        Long driverId = TestDataConstants.DRIVER_ID;
        String messageKey = RideExceptionMessageKeys.RIDE_NOT_FOUND_MESSAGE;
        Object[] args = new Object[]{driverId};
        String message = messageSource.getMessage(messageKey, args, currentLocale);

        when(rideService.getAllRidesByDriver(anyInt(), anyInt(), anyLong(), anyString()))
            .thenThrow(new RideNotFoundException(messageKey, args));

        mockMvc.perform(withLocale(get(TestDataConstants.GET_ALL_RIDES_ENDPOINT + "/drivers/{driverId}", driverId), currentLocale))
            .andExpect(status().isNotFound())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.message").value(message))
            .andExpect(jsonPath("$.status").value(TestDataConstants.HTTP_STATUS_NOT_FOUND))
            .andExpect(jsonPath("$.timestamp").exists());

        verify(rideService).getAllRidesByDriver(anyInt(), anyInt(), anyLong(), anyString());
    }

    @Test
    void getAllRidesByPassenger_ReturnsPageRideResponse_ValidRequest() throws Exception {
        Long passengerId = TestDataConstants.PASSENGER_ID;
        PageResponse<RideResponse> response = new PageResponse<>(
            TestDataConstants.PAGE_OFFSET,
            TestDataConstants.PAGE_LIMIT,
            1,
            1L,
            "sort",
            List.of(TestDataConstants.RIDE_RESPONSE)
        );
        when(rideService.getAllRidesByPassenger(anyInt(), anyInt(), anyLong(), anyString()))
            .thenReturn(response);

        mockMvc.perform(get(TestDataConstants.GET_ALL_RIDES_ENDPOINT + "/passengers/{passengerId}", passengerId))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(content().json(objectMapper.writeValueAsString(response)));
    }

    @ParameterizedTest
    @MethodSource("supportedLocales")
    void getAllRidesByPassenger_ReturnsNotFound_WhenPassengerNotFound(String locale) throws Exception {
        Locale currentLocale = Locale.forLanguageTag(locale);
        Long passengerId = TestDataConstants.PASSENGER_ID;
        String messageKey = RideExceptionMessageKeys.RIDE_NOT_FOUND_MESSAGE;
        Object[] args = new Object[]{passengerId};
        String message = messageSource.getMessage(messageKey, args, currentLocale);

        when(rideService.getAllRidesByPassenger(anyInt(), anyInt(), anyLong(), anyString()))
            .thenThrow(new RideNotFoundException(messageKey, args));

        mockMvc.perform(withLocale(get(TestDataConstants.GET_ALL_RIDES_ENDPOINT + "/passengers/{passengerId}", passengerId), currentLocale))
            .andExpect(status().isNotFound())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.message").value(message))
            .andExpect(jsonPath("$.status").value(TestDataConstants.HTTP_STATUS_NOT_FOUND))
            .andExpect(jsonPath("$.timestamp").exists());

        verify(rideService).getAllRidesByPassenger(anyInt(), anyInt(), anyLong(), anyString());
    }

    @Test
    void deleteRideById_ReturnsNoContent_ValidRequest() throws Exception {
        Long rideId = TestDataConstants.RIDE_ID;

        MockHttpServletRequestBuilder requestBuilder = delete(TestDataConstants.DELETE_RIDE_ENDPOINT, rideId);

        mockMvc.perform(requestBuilder)
            .andExpect(status().isNoContent());
    }

    @ParameterizedTest
    @MethodSource("supportedLocales")
    void deleteRideById_ReturnsNotFound_WhenRideNotFound(String locale) throws Exception {
        Locale currentLocale = Locale.forLanguageTag(locale);
        Long rideId = TestDataConstants.RIDE_ID;
        String messageKey = RideExceptionMessageKeys.RIDE_NOT_FOUND_MESSAGE;
        Object[] args = new Object[]{rideId};
        String message = messageSource.getMessage(messageKey, args, currentLocale);

        doThrow(new RideNotFoundException(messageKey, args))
            .when(rideService).deleteRideById(rideId);

        mockMvc.perform(withLocale(delete(TestDataConstants.DELETE_RIDE_ENDPOINT, rideId), currentLocale))
            .andExpect(status().isNotFound())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.message").value(message))
            .andExpect(jsonPath("$.status").value(TestDataConstants.HTTP_STATUS_NOT_FOUND))
            .andExpect(jsonPath("$.timestamp").exists());

        verify(rideService).deleteRideById(rideId);
    }

    @Test
    void getRideById_ReturnsRideResponse_ValidRequest() throws Exception {
        Long rideId = TestDataConstants.RIDE_ID;
        RideResponse rideResponse = TestDataConstants.RIDE_RESPONSE;

        when(rideService.getRideById(rideId))
            .thenReturn(rideResponse);

        mockMvc.perform(get(TestDataConstants.GET_RIDE_BY_ID_ENDPOINT, rideId))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(content().json(objectMapper.writeValueAsString(rideResponse)));
    }

    @ParameterizedTest
    @MethodSource("supportedLocales")
    void getRideById_ReturnsNotFound_WhenRideNotFound(String locale) throws Exception {
        Locale currentLocale = Locale.forLanguageTag(locale);
        Long rideId = TestDataConstants.RIDE_ID;
        String messageKey = RideExceptionMessageKeys.RIDE_NOT_FOUND_MESSAGE;
        Object[] args = new Object[]{rideId};
        String message = messageSource.getMessage(messageKey, args, currentLocale);

        when(rideService.getRideById(rideId))
            .thenThrow(new RideNotFoundException(messageKey, args));

        mockMvc.perform(withLocale(get(TestDataConstants.GET_RIDE_BY_ID_ENDPOINT, rideId), currentLocale))
            .andExpect(status().isNotFound())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.message").value(message))
            .andExpect(jsonPath("$.status").value(TestDataConstants.HTTP_STATUS_NOT_FOUND))
            .andExpect(jsonPath("$.timestamp").exists());

        verify(rideService).getRideById(rideId);
    }

}