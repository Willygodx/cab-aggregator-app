package by.modsen.ratingservice.service.impl;

import by.modsen.ratingservice.client.ride.RideResponse;
import by.modsen.ratingservice.constants.ApplicationConstants;
import by.modsen.ratingservice.dto.AverageRatingMessage;
import by.modsen.ratingservice.dto.request.RatingRequest;
import by.modsen.ratingservice.dto.response.AverageRatingResponse;
import by.modsen.ratingservice.dto.response.PageResponse;
import by.modsen.ratingservice.dto.response.RatingResponse;
import by.modsen.ratingservice.kafka.producer.RatingProducer;
import by.modsen.ratingservice.mapper.PageResponseMapper;
import by.modsen.ratingservice.mapper.RatingMapper;
import by.modsen.ratingservice.model.Rating;
import by.modsen.ratingservice.model.enums.RatedBy;
import by.modsen.ratingservice.repository.RatingRepository;
import by.modsen.ratingservice.service.RatingService;
import by.modsen.ratingservice.service.component.validation.RatingServiceValidation;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RatingServiceImpl implements RatingService {

    private final RatingMapper ratingMapper;
    private final PageResponseMapper pageResponseMapper;
    private final RatingServiceValidation ratingServiceValidation;
    private final RatingRepository ratingRepository;
    private final RatingProducer ratingProducer;

    @Override
    public RatingResponse createRating(RatingRequest ratingRequest, String languageTag) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        JwtAuthenticationToken token = (JwtAuthenticationToken) authentication;
        String jwt = ApplicationConstants.TOKEN_BEARER_PART + token.getToken().getTokenValue();

        Long rideId = ratingRequest.rideId();

        RatedBy ratedBy = ratingServiceValidation.validateUserRoleAndId(token, rideId, languageTag, jwt);

        ratingServiceValidation.checkRatingExists(rideId, ratedBy);

        RideResponse rideResponse = ratingServiceValidation.getRideWithRoleChecks(rideId, languageTag, jwt, token);

        Rating rating = ratingMapper.toEntity(ratingRequest,
            rideResponse.driverId(),
            rideResponse.passengerId(),
            ratedBy);

        rating = ratingRepository.save(rating);

        return ratingMapper.toResponse(rating);
    }

    @Override
    public RatingResponse updateRating(RatingRequest ratingRequest, String ratingId) {
        Rating rating = ratingServiceValidation.getRatingWithChecks(ratingId);

        ratingMapper.updateRatingFromDto(ratingRequest, rating);

        rating = ratingRepository.save(rating);

        return ratingMapper.toResponse(rating);
    }

    @Override
    public RatingResponse getRatingById(String ratingId) {
        Rating rating = ratingServiceValidation.getRatingWithChecks(ratingId);

        return ratingMapper.toResponse(rating);
    }

    @Override
    public PageResponse<RatingResponse> getAllRatings(Integer offset, Integer limit) {
        Page<RatingResponse> ratingDtoPage = ratingRepository
            .findAll(PageRequest.of(offset, limit))
            .map(ratingMapper::toResponse);

        return pageResponseMapper.toDto(ratingDtoPage);
    }

    @Override
    public PageResponse<RatingResponse> getAllRatingsByPassenger(Integer offset, Integer limit, UUID passengerId) {
        ratingServiceValidation.checkRatingExistsByPassengerId(passengerId);

        Page<RatingResponse> ratingDtoPage = ratingRepository
            .findAllByPassengerIdAndRatedBy(passengerId, RatedBy.PASSENGER, PageRequest.of(offset, limit))
            .map(ratingMapper::toResponse);

        return pageResponseMapper.toDto(ratingDtoPage);
    }

    @Override
    public PageResponse<RatingResponse> getAllRatingsByDriver(Integer offset, Integer limit, UUID driverId) {
        ratingServiceValidation.checkRatingExistsByDriverId(driverId);

        Page<RatingResponse> ratingDtoPage = ratingRepository
            .findAllByDriverIdAndRatedBy(driverId, RatedBy.DRIVER, PageRequest.of(offset, limit))
            .map(ratingMapper::toResponse);

        return pageResponseMapper.toDto(ratingDtoPage);
    }

    @Override
    public AverageRatingResponse getAverageRatingForPassenger(UUID passengerId, String languageTag) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        JwtAuthenticationToken token = (JwtAuthenticationToken) authentication;
        String jwt = ApplicationConstants.TOKEN_BEARER_PART + token.getToken().getTokenValue();

        ratingServiceValidation.checkPassengerExists(passengerId, languageTag, jwt);

        Double averageRating = calculateAverageRating(passengerId, RatedBy.DRIVER);

        return new AverageRatingResponse(roundToTwoDecimalPlaces(averageRating));
    }

    @Override
    public AverageRatingResponse getAverageRatingForDriver(UUID driverId, String languageTag) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        JwtAuthenticationToken token = (JwtAuthenticationToken) authentication;
        String jwt = ApplicationConstants.TOKEN_BEARER_PART + token.getToken().getTokenValue();

        ratingServiceValidation.checkDriverExists(driverId, languageTag, jwt);

        Double averageRating = calculateAverageRating(driverId, RatedBy.PASSENGER);

        return new AverageRatingResponse(roundToTwoDecimalPlaces(averageRating));
    }

    @Override
    public void deleteRating(String ratingId) {
        ratingServiceValidation.checkRatingExistsById(ratingId);

        ratingRepository.deleteById(ratingId);
    }

    @Scheduled(cron = ApplicationConstants.SCHEDULED_CRON)
    @Override
    public void calculateAndSendAverageRatings() {
        List<UUID> driverIds = getDistinctDriverIds();
        List<UUID> passengerIds = getDistinctPassengerIds();

        driverIds.forEach(driverId -> {
            Double averageRating = calculateAverageRating(driverId, RatedBy.PASSENGER);
            AverageRatingMessage message = new AverageRatingMessage(driverId,
                                                                    roundToTwoDecimalPlaces(averageRating));
            ratingProducer.sendDriverAverageRating(message);
        });

        passengerIds.forEach(passengerId -> {
            Double averageRating = calculateAverageRating(passengerId, RatedBy.DRIVER);
            AverageRatingMessage message = new AverageRatingMessage(passengerId,
                                                                    roundToTwoDecimalPlaces(averageRating));
            ratingProducer.sendPassengerAverageRating(message);
        });
    }

    private double calculateAverageRating(UUID userId, RatedBy ratedBy) {
        List<Rating> ratings;

        if (ratedBy == RatedBy.DRIVER) {
            ratings = ratingRepository.findAllByPassengerIdAndRatedBy(userId, RatedBy.DRIVER);
        } else if (ratedBy == RatedBy.PASSENGER) {
            ratings = ratingRepository.findAllByDriverIdAndRatedBy(userId, RatedBy.PASSENGER);
        } else {
            return 0.0;
        }

        return ratings.stream()
            .mapToInt(Rating::getMark)
            .average()
            .orElse(0.0);
    }

    private List<UUID> getDistinctDriverIds() {
        return ratingRepository.findAllByDriverIdIsNotNull()
            .stream()
            .map(Rating::getDriverId)
            .distinct()
            .toList();
    }

    private List<UUID> getDistinctPassengerIds() {
        return ratingRepository.findAllByPassengerIdIsNotNull()
            .stream()
            .map(Rating::getPassengerId)
            .distinct()
            .toList();
    }

    private double roundToTwoDecimalPlaces(Double value) {
        return new BigDecimal(value)
            .setScale(2, RoundingMode.HALF_UP)
            .doubleValue();
    }

}
