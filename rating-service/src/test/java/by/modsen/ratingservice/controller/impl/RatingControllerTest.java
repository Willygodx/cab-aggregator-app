package by.modsen.ratingservice.controller.impl;

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

import by.modsen.ratingservice.configuration.TestConfig;
import by.modsen.ratingservice.constants.RatingExceptionMessageKeys;
import by.modsen.ratingservice.constants.TestDataConstants;
import by.modsen.ratingservice.dto.request.RatingRequest;
import by.modsen.ratingservice.dto.response.AverageRatingResponse;
import by.modsen.ratingservice.dto.response.PageResponse;
import by.modsen.ratingservice.dto.response.RatingResponse;
import by.modsen.ratingservice.exception.rating.RatingAlreadyExistsException;
import by.modsen.ratingservice.exception.rating.RatingNotFoundException;
import by.modsen.ratingservice.service.RatingService;
import by.modsen.ratingservice.utils.LocaleUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
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

@WebMvcTest(RatingController.class)
@Import(TestConfig.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class RatingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private RatingService ratingService;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private ResourcePatternResolver resourcePatternResolver;

    private Set<Locale> supportedLocales;

    @BeforeAll
    void initSupportedLocales() throws Exception {
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
    void createRating_ReturnsCreatedRatingResponse_ValidRequest() throws Exception {
        RatingRequest ratingRequest = TestDataConstants.RATING_REQUEST;
        RatingResponse ratingResponse = TestDataConstants.RATING_RESPONSE;

        MockHttpServletRequestBuilder requestBuilder = post(TestDataConstants.CREATE_RATING_ENDPOINT)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(ratingRequest));
        when(ratingService.createRating(ratingRequest, TestDataConstants.LANGUAGE_TAG))
            .thenReturn(ratingResponse);

        mockMvc.perform(requestBuilder)
            .andExpect(status().isCreated())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(content().json(objectMapper.writeValueAsString(ratingResponse)));
    }

    @ParameterizedTest
    @MethodSource("supportedLocales")
    void createRating_ReturnsConflict_WhenRatingAlreadyExists(String locale) throws Exception {
        Locale currentLocale = Locale.forLanguageTag(locale);
        RatingRequest ratingRequest = TestDataConstants.RATING_REQUEST;
        String messageKey = RatingExceptionMessageKeys.RATING_ALREADY_EXISTS_MESSAGE;
        Object[] args = new Object[] {TestDataConstants.RIDE_ID, TestDataConstants.RATED_BY_DRIVER.toLowerCase()};
        String message = messageSource.getMessage(messageKey, args, currentLocale);

        MockHttpServletRequestBuilder requestBuilder = post(TestDataConstants.CREATE_RATING_ENDPOINT)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(ratingRequest));
        when(ratingService.createRating(ratingRequest, currentLocale.toLanguageTag()))
            .thenThrow(new RatingAlreadyExistsException(messageKey, args));

        mockMvc.perform(withLocale(requestBuilder, currentLocale))
            .andExpect(status().isConflict())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.message").value(message))
            .andExpect(jsonPath("$.status").value(TestDataConstants.HTTP_STATUS_CONFLICT))
            .andExpect(jsonPath("$.timestamp").exists());

        verify(ratingService).createRating(ratingRequest, currentLocale.toLanguageTag());
    }

    @Test
    void updateRating_ReturnsUpdatedRatingResponse_ValidRequest() throws Exception {
        String ratingId = TestDataConstants.RATING_ID;
        RatingRequest ratingRequest = TestDataConstants.RATING_REQUEST_FOR_UPDATE;
        RatingResponse ratingResponse = TestDataConstants.RATING_RESPONSE;

        MockHttpServletRequestBuilder requestBuilder = put(TestDataConstants.UPDATE_RATING_ENDPOINT, ratingId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(ratingRequest));
        when(ratingService.updateRating(ratingRequest, ratingId))
            .thenReturn(ratingResponse);

        mockMvc.perform(requestBuilder)
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(content().json(objectMapper.writeValueAsString(ratingResponse)));
    }

    @ParameterizedTest
    @MethodSource("supportedLocales")
    void updateRating_ReturnsNotFound_WhenRatingNotFound(String locale) throws Exception {
        Locale currentLocale = Locale.forLanguageTag(locale);
        String ratingId = TestDataConstants.RATING_ID;
        RatingRequest ratingRequest = TestDataConstants.RATING_REQUEST_FOR_UPDATE;
        String messageKey = RatingExceptionMessageKeys.RATING_NOT_FOUND_MESSAGE;
        Object[] args = new Object[] {ratingId};
        String message = messageSource.getMessage(messageKey, args, currentLocale);

        MockHttpServletRequestBuilder requestBuilder = put(TestDataConstants.UPDATE_RATING_ENDPOINT, ratingId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(ratingRequest));
        when(ratingService.updateRating(ratingRequest, ratingId))
            .thenThrow(new RatingNotFoundException(messageKey, args));

        mockMvc.perform(withLocale(requestBuilder, currentLocale))
            .andExpect(status().isNotFound())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.message").value(message))
            .andExpect(jsonPath("$.status").value(TestDataConstants.HTTP_STATUS_NOT_FOUND))
            .andExpect(jsonPath("$.timestamp").exists());

        verify(ratingService).updateRating(ratingRequest, ratingId);
    }

    @Test
    void deleteRating_ReturnsNoContent_ValidRequest() throws Exception {
        String ratingId = TestDataConstants.RATING_ID;

        MockHttpServletRequestBuilder requestBuilder = delete(TestDataConstants.DELETE_RATING_ENDPOINT, ratingId);

        mockMvc.perform(requestBuilder)
            .andExpect(status().isNoContent());
    }

    @ParameterizedTest
    @MethodSource("supportedLocales")
    void deleteRating_ReturnsNotFound_WhenRatingNotFound(String locale) throws Exception {
        Locale currentLocale = Locale.forLanguageTag(locale);
        String ratingId = TestDataConstants.RATING_ID;
        String messageKey = RatingExceptionMessageKeys.RATING_NOT_FOUND_MESSAGE;
        Object[] args = new Object[] {ratingId};
        String message = messageSource.getMessage(messageKey, args, currentLocale);

        doThrow(new RatingNotFoundException(messageKey, args))
            .when(ratingService).deleteRating(ratingId);

        mockMvc.perform(withLocale(delete(TestDataConstants.DELETE_RATING_ENDPOINT, ratingId), currentLocale))
            .andExpect(status().isNotFound())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.message").value(message))
            .andExpect(jsonPath("$.status").value(TestDataConstants.HTTP_STATUS_NOT_FOUND))
            .andExpect(jsonPath("$.timestamp").exists());

        verify(ratingService).deleteRating(ratingId);
    }

    @Test
    void getRatingById_ReturnsRatingResponse_ValidRequest() throws Exception {
        String ratingId = TestDataConstants.RATING_ID;
        RatingResponse ratingResponse = TestDataConstants.RATING_RESPONSE;

        when(ratingService.getRatingById(ratingId))
            .thenReturn(ratingResponse);

        mockMvc.perform(get(TestDataConstants.GET_RATING_BY_ID_ENDPOINT, ratingId))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(content().json(objectMapper.writeValueAsString(ratingResponse)));
    }

    @ParameterizedTest
    @MethodSource("supportedLocales")
    void getRatingById_ReturnsNotFound_WhenRatingNotFound(String locale) throws Exception {
        Locale currentLocale = Locale.forLanguageTag(locale);
        String ratingId = TestDataConstants.RATING_ID;
        String messageKey = RatingExceptionMessageKeys.RATING_NOT_FOUND_MESSAGE;
        Object[] args = new Object[] {ratingId};
        String message = messageSource.getMessage(messageKey, args, currentLocale);

        when(ratingService.getRatingById(ratingId))
            .thenThrow(new RatingNotFoundException(messageKey, args));

        mockMvc.perform(withLocale(get(TestDataConstants.GET_RATING_BY_ID_ENDPOINT, ratingId), currentLocale))
            .andExpect(status().isNotFound())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.message").value(message))
            .andExpect(jsonPath("$.status").value(TestDataConstants.HTTP_STATUS_NOT_FOUND))
            .andExpect(jsonPath("$.timestamp").exists());

        verify(ratingService).getRatingById(ratingId);
    }

    @Test
    void getAllRatings_ReturnsPageRatingResponse_ValidRequest() throws Exception {
        PageResponse<RatingResponse> response = new PageResponse<>(
            TestDataConstants.PAGE_OFFSET,
            TestDataConstants.PAGE_LIMIT,
            1,
            1L,
            "sort",
            List.of(TestDataConstants.RATING_RESPONSE)
        );
        when(ratingService.getAllRatings(anyInt(), anyInt()))
            .thenReturn(response);

        mockMvc.perform(get(TestDataConstants.GET_ALL_RATINGS_ENDPOINT))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(content().json(objectMapper.writeValueAsString(response)));
    }

    @Test
    void getAllRatingsByPassenger_ReturnsPageRatingResponse_ValidRequest() throws Exception {
        Long passengerId = TestDataConstants.PASSENGER_ID;
        PageResponse<RatingResponse> response = new PageResponse<>(
            TestDataConstants.PAGE_OFFSET,
            TestDataConstants.PAGE_LIMIT,
            1,
            1L,
            "sort",
            List.of(TestDataConstants.RATING_RESPONSE)
        );
        when(ratingService.getAllRatingsByPassenger(anyInt(), anyInt(), anyLong()))
            .thenReturn(response);

        mockMvc.perform(get(TestDataConstants.GET_ALL_RATINGS_ENDPOINT + "/passengers/{passengerId}", passengerId))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(content().json(objectMapper.writeValueAsString(response)));
    }

    @ParameterizedTest
    @MethodSource("supportedLocales")
    void getAllRatingsByPassenger_ReturnsNotFound_WhenPassengerNotFound(String locale) throws Exception {
        Locale currentLocale = Locale.forLanguageTag(locale);
        Long passengerId = TestDataConstants.PASSENGER_ID;
        String messageKey = RatingExceptionMessageKeys.RATING_FROM_PASSENGER_NOT_FOUND_MESSAGE;
        Object[] args = new Object[] {passengerId};
        String message = messageSource.getMessage(messageKey, args, currentLocale);

        when(ratingService.getAllRatingsByPassenger(anyInt(), anyInt(), anyLong()))
            .thenThrow(new RatingNotFoundException(messageKey, args));

        mockMvc.perform(
                withLocale(get(TestDataConstants.GET_ALL_RATINGS_ENDPOINT + "/passengers/{passengerId}", passengerId),
                    currentLocale))
            .andExpect(status().isNotFound())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.message").value(message))
            .andExpect(jsonPath("$.status").value(TestDataConstants.HTTP_STATUS_NOT_FOUND))
            .andExpect(jsonPath("$.timestamp").exists());

        verify(ratingService).getAllRatingsByPassenger(anyInt(), anyInt(), anyLong());
    }

    @Test
    void getAllRatingsByDriver_ReturnsPageRatingResponse_ValidRequest() throws Exception {
        Long driverId = TestDataConstants.DRIVER_ID;
        PageResponse<RatingResponse> response = new PageResponse<>(
            TestDataConstants.PAGE_OFFSET,
            TestDataConstants.PAGE_LIMIT,
            1,
            1L,
            "sort",
            List.of(TestDataConstants.RATING_RESPONSE)
        );
        when(ratingService.getAllRatingsByDriver(anyInt(), anyInt(), anyLong()))
            .thenReturn(response);

        mockMvc.perform(get(TestDataConstants.GET_ALL_RATINGS_ENDPOINT + "/drivers/{driverId}", driverId))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(content().json(objectMapper.writeValueAsString(response)));
    }

    @ParameterizedTest
    @MethodSource("supportedLocales")
    void getAllRatingsByDriver_ReturnsNotFound_WhenDriverNotFound(String locale) throws Exception {
        Locale currentLocale = Locale.forLanguageTag(locale);
        Long driverId = TestDataConstants.DRIVER_ID;
        String messageKey = RatingExceptionMessageKeys.RATING_FROM_DRIVER_NOT_FOUND_MESSAGE;
        Object[] args = new Object[] {driverId};
        String message = messageSource.getMessage(messageKey, args, currentLocale);

        when(ratingService.getAllRatingsByDriver(anyInt(), anyInt(), anyLong()))
            .thenThrow(new RatingNotFoundException(messageKey, args));

        mockMvc.perform(withLocale(get(TestDataConstants.GET_ALL_RATINGS_ENDPOINT + "/drivers/{driverId}", driverId),
                currentLocale))
            .andExpect(status().isNotFound())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.message").value(message))
            .andExpect(jsonPath("$.status").value(TestDataConstants.HTTP_STATUS_NOT_FOUND))
            .andExpect(jsonPath("$.timestamp").exists());

        verify(ratingService).getAllRatingsByDriver(anyInt(), anyInt(), anyLong());
    }

    @Test
    void getAverageRatingForPassenger_ReturnsAverageRatingResponse_ValidRequest() throws Exception {
        Long passengerId = TestDataConstants.PASSENGER_ID;
        AverageRatingResponse response = TestDataConstants.AVERAGE_RATING_RESPONSE;

        when(ratingService.getAverageRatingForPassenger(passengerId, TestDataConstants.LANGUAGE_TAG))
            .thenReturn(response);

        mockMvc.perform(get(TestDataConstants.GET_ALL_RATINGS_ENDPOINT + "/passengers/{passengerId}/average-rating", passengerId))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(content().json(objectMapper.writeValueAsString(response)));
    }

    @Test
    void getAverageRatingForDriver_ReturnsAverageRatingResponse_ValidRequest() throws Exception {
        Long driverId = TestDataConstants.DRIVER_ID;
        AverageRatingResponse response = TestDataConstants.AVERAGE_RATING_RESPONSE;

        when(ratingService.getAverageRatingForDriver(driverId, TestDataConstants.LANGUAGE_TAG))
            .thenReturn(response);

        mockMvc.perform(get(TestDataConstants.GET_ALL_RATINGS_ENDPOINT + "/drivers/{driverId}/average-rating", driverId))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(content().json(objectMapper.writeValueAsString(response)));
    }

    @Test
    void triggerCalculateAndSendAverageRatings_ReturnsNoContent_ValidRequest() throws Exception {
        MockHttpServletRequestBuilder requestBuilder = post(TestDataConstants.GET_ALL_RATINGS_ENDPOINT + "/test/trigger-kafka");

        mockMvc.perform(requestBuilder)
            .andExpect(status().isOk());
    }

}