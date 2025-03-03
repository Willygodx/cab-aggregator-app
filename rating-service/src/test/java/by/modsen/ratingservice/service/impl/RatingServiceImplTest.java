package by.modsen.ratingservice.service.impl;

import by.modsen.ratingservice.client.ride.RideResponse;
import by.modsen.ratingservice.constants.TestDataConstants;
import by.modsen.ratingservice.dto.AverageRatingMessage;
import by.modsen.ratingservice.dto.request.RatingRequest;
import by.modsen.ratingservice.dto.response.AverageRatingResponse;
import by.modsen.ratingservice.dto.response.PageResponse;
import by.modsen.ratingservice.dto.response.RatingResponse;
import by.modsen.ratingservice.exception.rating.RatingAlreadyExistsException;
import by.modsen.ratingservice.exception.rating.RatingNotFoundException;
import by.modsen.ratingservice.kafka.producer.RatingProducer;
import by.modsen.ratingservice.mapper.PageResponseMapper;
import by.modsen.ratingservice.mapper.RatingMapper;
import by.modsen.ratingservice.model.Rating;
import by.modsen.ratingservice.model.enums.RatedBy;
import by.modsen.ratingservice.repository.RatingRepository;
import by.modsen.ratingservice.service.component.validation.RatingServiceValidation;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RatingServiceImplTest {

    @Mock
    private RatingRepository ratingRepository;

    @Mock
    private RatingMapper ratingMapper;

    @Mock
    private PageResponseMapper pageResponseMapper;

    @Mock
    private RatingServiceValidation ratingServiceValidation;

    @Mock
    private RatingProducer ratingProducer;

    @InjectMocks
    private RatingServiceImpl ratingService;

    @Test
    void createRating_ReturnsRatingResponse_ValidInputArguments() {
        RatingRequest ratingRequest = TestDataConstants.RATING_REQUEST;
        Rating rating = TestDataConstants.RATING;
        RatingResponse expectedResponse = TestDataConstants.RATING_RESPONSE;
        RideResponse rideResponse = new RideResponse(
            TestDataConstants.RIDE_ID,
            TestDataConstants.DRIVER_ID,
            TestDataConstants.PASSENGER_ID,
            TestDataConstants.PICKUP_ADDRESS,
            TestDataConstants.DESTINATION_ADDRESS,
            TestDataConstants.RIDE_STATUS,
            LocalDateTime.now(),
            new BigDecimal("10.00")
        );

        doNothing().when(ratingServiceValidation).checkRatingExists(TestDataConstants.RIDE_ID, RatedBy.DRIVER);
        when(ratingServiceValidation.getRideWithChecks(TestDataConstants.RIDE_ID, TestDataConstants.LANGUAGE_TAG)).thenReturn(rideResponse);
        when(ratingMapper.toEntity(ratingRequest, TestDataConstants.DRIVER_ID, TestDataConstants.PASSENGER_ID, RatedBy.DRIVER)).thenReturn(rating);
        when(ratingRepository.save(rating)).thenReturn(rating);
        when(ratingMapper.toResponse(rating)).thenReturn(expectedResponse);

        RatingResponse actual = ratingService.createRating(ratingRequest, TestDataConstants.LANGUAGE_TAG);

        assertThat(actual).isEqualTo(expectedResponse);
        verify(ratingServiceValidation).checkRatingExists(TestDataConstants.RIDE_ID, RatedBy.DRIVER);
        verify(ratingServiceValidation).getRideWithChecks(TestDataConstants.RIDE_ID, TestDataConstants.LANGUAGE_TAG);
        verify(ratingMapper).toEntity(ratingRequest, TestDataConstants.DRIVER_ID, TestDataConstants.PASSENGER_ID, RatedBy.DRIVER);
        verify(ratingRepository).save(rating);
        verify(ratingMapper).toResponse(rating);
    }

    @Test
    void createRating_ThrowsException_RatingAlreadyExists() {
        RatingRequest ratingRequest = TestDataConstants.RATING_REQUEST;
        String messageKey = TestDataConstants.RATING_ALREADY_EXISTS_MESSAGE;

        doThrow(new RatingAlreadyExistsException(messageKey))
            .when(ratingServiceValidation).checkRatingExists(TestDataConstants.RIDE_ID, RatedBy.DRIVER);

        assertThatThrownBy(() -> ratingService.createRating(ratingRequest, TestDataConstants.LANGUAGE_TAG))
            .isInstanceOf(RatingAlreadyExistsException.class)
            .hasMessage(messageKey);

        verify(ratingServiceValidation).checkRatingExists(TestDataConstants.RIDE_ID, RatedBy.DRIVER);
        verify(ratingRepository, never()).save(any(Rating.class));
    }

    @Test
    void updateRating_ReturnsRatingResponse_ValidInputArguments() {
        RatingRequest ratingRequest = TestDataConstants.RATING_REQUEST;
        Rating rating = TestDataConstants.RATING;
        RatingResponse expectedResponse = TestDataConstants.RATING_RESPONSE;

        when(ratingServiceValidation.getRatingWithChecks(TestDataConstants.RATING_ID)).thenReturn(rating);
        when(ratingRepository.save(rating)).thenReturn(rating);
        when(ratingMapper.toResponse(rating)).thenReturn(expectedResponse);

        RatingResponse actual = ratingService.updateRating(ratingRequest, TestDataConstants.RATING_ID);

        assertThat(actual).isEqualTo(expectedResponse);
        verify(ratingServiceValidation).getRatingWithChecks(TestDataConstants.RATING_ID);
        verify(ratingMapper).updateRatingFromDto(ratingRequest, rating);
        verify(ratingRepository).save(rating);
        verify(ratingMapper).toResponse(rating);
    }

    @Test
    void updateRating_ThrowsException_RatingNotFound() {
        RatingRequest ratingRequest = TestDataConstants.RATING_REQUEST;
        String messageKey = TestDataConstants.RATING_NOT_FOUND_MESSAGE;

        when(ratingServiceValidation.getRatingWithChecks(TestDataConstants.RATING_ID))
            .thenThrow(new RatingNotFoundException(messageKey));

        assertThatThrownBy(() -> ratingService.updateRating(ratingRequest, TestDataConstants.RATING_ID))
            .isInstanceOf(RatingNotFoundException.class)
            .hasMessage(messageKey);

        verify(ratingServiceValidation).getRatingWithChecks(TestDataConstants.RATING_ID);
        verify(ratingRepository, never()).save(any(Rating.class));
    }

    @Test
    void getRatingById_ReturnsRatingResponse_ValidInputArguments() {
        Rating rating = TestDataConstants.RATING;
        RatingResponse expectedResponse = TestDataConstants.RATING_RESPONSE;

        when(ratingServiceValidation.getRatingWithChecks(TestDataConstants.RATING_ID)).thenReturn(rating);
        when(ratingMapper.toResponse(rating)).thenReturn(expectedResponse);

        RatingResponse actual = ratingService.getRatingById(TestDataConstants.RATING_ID);

        assertThat(actual).isEqualTo(expectedResponse);
        verify(ratingServiceValidation).getRatingWithChecks(TestDataConstants.RATING_ID);
        verify(ratingMapper).toResponse(rating);
    }

    @Test
    void getRatingById_ThrowsException_RatingNotFound() {
        String messageKey = TestDataConstants.RATING_NOT_FOUND_MESSAGE;

        when(ratingServiceValidation.getRatingWithChecks(TestDataConstants.RATING_ID))
            .thenThrow(new RatingNotFoundException(messageKey));

        assertThatThrownBy(() -> ratingService.getRatingById(TestDataConstants.RATING_ID))
            .isInstanceOf(RatingNotFoundException.class)
            .hasMessage(messageKey);

        verify(ratingServiceValidation).getRatingWithChecks(TestDataConstants.RATING_ID);
        verify(ratingMapper, never()).toResponse(any(Rating.class));
    }

    @Test
    void getAllRatings_ReturnsPageRatingResponse_ValidInputArguments() {
        int offset = TestDataConstants.PAGE_OFFSET;
        int limit = TestDataConstants.PAGE_LIMIT;
        PageResponse<RatingResponse> expectedResponse = PageResponse.<RatingResponse>builder()
            .addCurrentOffset(offset)
            .addCurrentLimit(limit)
            .addTotalPages(1)
            .addTotalElements(1)
            .addSort("id")
            .addValues(Collections.singletonList(TestDataConstants.RATING_RESPONSE))
            .build();
        List<Rating> ratings = Collections.singletonList(TestDataConstants.RATING);
        Page<Rating> ratingPage = new PageImpl<>(ratings);
        when(ratingRepository.findAll(any(Pageable.class))).thenReturn(ratingPage);
        arrangeForTestingPageMethods(expectedResponse);

        PageResponse<RatingResponse> actual = ratingService.getAllRatings(offset, limit);

        assertThat(actual).isSameAs(expectedResponse);
        verify(ratingMapper).toResponse(TestDataConstants.RATING);
        verify(pageResponseMapper).toDto(ArgumentMatchers.<Page<RatingResponse>>any());
    }

    @Test
    void getAllRatingsByPassenger_ReturnsPageRatingResponse_ValidInputArguments() {
        int offset = TestDataConstants.PAGE_OFFSET;
        int limit = TestDataConstants.PAGE_LIMIT;
        Long passengerId = TestDataConstants.PASSENGER_ID;
        PageResponse<RatingResponse> expectedResponse = PageResponse.<RatingResponse>builder()
            .addCurrentOffset(offset)
            .addCurrentLimit(limit)
            .addTotalPages(1)
            .addTotalElements(1)
            .addSort("id")
            .addValues(Collections.singletonList(TestDataConstants.RATING_RESPONSE))
            .build();
        List<Rating> ratings = Collections.singletonList(TestDataConstants.RATING);
        Page<Rating> ratingPage = new PageImpl<>(ratings);
        doNothing().when(ratingServiceValidation).checkRatingExistsByPassengerId(passengerId);
        when(ratingRepository.findAllByPassengerIdAndRatedBy(passengerId, RatedBy.PASSENGER, PageRequest.of(offset, limit))).thenReturn(ratingPage);
        arrangeForTestingPageMethods(expectedResponse);

        PageResponse<RatingResponse> actual = ratingService.getAllRatingsByPassenger(offset, limit, passengerId);

        assertThat(actual).isSameAs(expectedResponse);
        verify(ratingServiceValidation).checkRatingExistsByPassengerId(passengerId);
        verify(ratingMapper).toResponse(TestDataConstants.RATING);
        verify(pageResponseMapper).toDto(ArgumentMatchers.<Page<RatingResponse>>any());
    }

    @Test
    void getAllRatingsByDriver_ReturnsPageRatingResponse_ValidInputArguments() {
        int offset = TestDataConstants.PAGE_OFFSET;
        int limit = TestDataConstants.PAGE_LIMIT;
        Long driverId = TestDataConstants.DRIVER_ID;
        PageResponse<RatingResponse> expectedResponse = PageResponse.<RatingResponse>builder()
            .addCurrentOffset(offset)
            .addCurrentLimit(limit)
            .addTotalPages(1)
            .addTotalElements(1)
            .addSort("id")
            .addValues(Collections.singletonList(TestDataConstants.RATING_RESPONSE))
            .build();
        List<Rating> ratings = Collections.singletonList(TestDataConstants.RATING);
        Page<Rating> ratingPage = new PageImpl<>(ratings);
        doNothing().when(ratingServiceValidation).checkRatingExistsByDriverId(driverId);
        when(ratingRepository.findAllByDriverIdAndRatedBy(driverId, RatedBy.DRIVER, PageRequest.of(offset, limit))).thenReturn(ratingPage);
        arrangeForTestingPageMethods(expectedResponse);

        PageResponse<RatingResponse> actual = ratingService.getAllRatingsByDriver(offset, limit, driverId);

        assertThat(actual).isSameAs(expectedResponse);
        verify(ratingServiceValidation).checkRatingExistsByDriverId(driverId);
        verify(ratingMapper).toResponse(TestDataConstants.RATING);
        verify(pageResponseMapper).toDto(ArgumentMatchers.<Page<RatingResponse>>any());
    }

    @Test
    void getAverageRatingForPassenger_ReturnsAverageRatingResponse_ValidInputArguments() {
        Long passengerId = TestDataConstants.PASSENGER_ID;
        AverageRatingResponse expectedResponse = TestDataConstants.AVERAGE_RATING_RESPONSE;

        doNothing().when(ratingServiceValidation).checkPassengerExists(passengerId, TestDataConstants.LANGUAGE_TAG);
        when(ratingRepository.findAllByPassengerIdAndRatedBy(passengerId, RatedBy.DRIVER)).thenReturn(Collections.singletonList(TestDataConstants.RATING));

        AverageRatingResponse actual = ratingService.getAverageRatingForPassenger(passengerId, TestDataConstants.LANGUAGE_TAG);

        assertThat(actual).isEqualTo(expectedResponse);
        verify(ratingServiceValidation).checkPassengerExists(passengerId, TestDataConstants.LANGUAGE_TAG);
        verify(ratingRepository).findAllByPassengerIdAndRatedBy(passengerId, RatedBy.DRIVER);
    }

    @Test
    void getAverageRatingForDriver_ReturnsAverageRatingResponse_ValidInputArguments() {
        Long driverId = TestDataConstants.DRIVER_ID;
        AverageRatingResponse expectedResponse = TestDataConstants.AVERAGE_RATING_RESPONSE;

        doNothing().when(ratingServiceValidation).checkDriverExists(driverId, TestDataConstants.LANGUAGE_TAG);
        when(ratingRepository.findAllByDriverIdAndRatedBy(driverId, RatedBy.PASSENGER)).thenReturn(Collections.singletonList(TestDataConstants.RATING));

        AverageRatingResponse actual = ratingService.getAverageRatingForDriver(driverId, TestDataConstants.LANGUAGE_TAG);

        assertThat(actual).isEqualTo(expectedResponse);
        verify(ratingServiceValidation).checkDriverExists(driverId, TestDataConstants.LANGUAGE_TAG);
        verify(ratingRepository).findAllByDriverIdAndRatedBy(driverId, RatedBy.PASSENGER);
    }

    @Test
    void deleteRatingById_MarksRatingAsDeleted_ValidInputArguments() {
        String ratingId = TestDataConstants.RATING_ID;

        doNothing().when(ratingServiceValidation).checkRatingExistsById(ratingId);
        doNothing().when(ratingRepository).deleteById(ratingId);

        ratingService.deleteRating(ratingId);

        verify(ratingServiceValidation).checkRatingExistsById(ratingId);
        verify(ratingRepository).deleteById(ratingId);
    }

    @Test
    void deleteRatingById_ThrowsException_RatingNotFound() {
        String ratingId = TestDataConstants.RATING_ID;
        String messageKey = TestDataConstants.RATING_NOT_FOUND_MESSAGE;

        doThrow(new RatingNotFoundException(messageKey))
            .when(ratingServiceValidation).checkRatingExistsById(ratingId);

        assertThatThrownBy(() -> ratingService.deleteRating(ratingId))
            .isInstanceOf(RatingNotFoundException.class)
            .hasMessage(messageKey);

        verify(ratingServiceValidation).checkRatingExistsById(ratingId);
        verify(ratingRepository, never()).deleteById(any(String.class));
    }

    @Test
    void calculateAndSendAverageRatings_SendsMessagesToKafka_ValidInputArguments() {
        when(ratingRepository.findAllByDriverIdIsNotNull()).thenReturn(Collections.singletonList(TestDataConstants.RATING));
        when(ratingRepository.findAllByPassengerIdIsNotNull()).thenReturn(Collections.singletonList(TestDataConstants.RATING));

        ratingService.calculateAndSendAverageRatings();

        verify(ratingRepository).findAllByDriverIdIsNotNull();
        verify(ratingRepository).findAllByPassengerIdIsNotNull();
        verify(ratingProducer).sendDriverAverageRating(any(AverageRatingMessage.class));
        verify(ratingProducer).sendPassengerAverageRating(any(AverageRatingMessage.class));
    }

    private void arrangeForTestingPageMethods(PageResponse<RatingResponse> expectedResponse) {
        when(ratingMapper.toResponse(any(Rating.class)))
            .thenReturn(TestDataConstants.RATING_RESPONSE);
        when(pageResponseMapper.toDto(ArgumentMatchers.<Page<RatingResponse>>any()))
            .thenReturn(expectedResponse);
    }

}