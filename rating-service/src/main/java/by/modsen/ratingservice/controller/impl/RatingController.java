package by.modsen.ratingservice.controller.impl;

import by.modsen.ratingservice.aspect.ValidateAccessDriver;
import by.modsen.ratingservice.aspect.ValidateAccessPassenger;
import by.modsen.ratingservice.controller.RatingOperations;
import by.modsen.ratingservice.dto.Marker;
import by.modsen.ratingservice.dto.request.RatingRequest;
import by.modsen.ratingservice.dto.response.AverageRatingResponse;
import by.modsen.ratingservice.dto.response.PageResponse;
import by.modsen.ratingservice.dto.response.RatingResponse;
import by.modsen.ratingservice.service.RatingService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/ratings")
@Validated
public class RatingController implements RatingOperations {

    private final RatingService ratingService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Validated({Marker.OnCreate.class})
    @PreAuthorize("hasRole('ADMIN') or hasRole('PASSENGER') or hasRole('DRIVER')")
    public RatingResponse createRating(@RequestBody @Valid RatingRequest ratingRequest) {
        String languageTag = LocaleContextHolder.getLocale().toLanguageTag();

        return ratingService.createRating(ratingRequest, languageTag);
    }

    @PutMapping("/{ratingId}")
    @Validated({Marker.OnUpdate.class})
    @PreAuthorize("hasRole('ADMIN') or hasRole('PASSENGER') or hasRole('DRIVER')")
    public RatingResponse updateRating(@RequestBody @Valid RatingRequest ratingRequest,
                                       @PathVariable String ratingId) {
        return ratingService.updateRating(ratingRequest, ratingId);
    }

    @DeleteMapping("/{ratingId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteRating(@PathVariable String ratingId) {
        ratingService.deleteRating(ratingId);
    }

    @GetMapping("/{ratingId}")
    public RatingResponse getRatingById(@PathVariable String ratingId) {
        return ratingService.getRatingById(ratingId);
    }

    @GetMapping()
    public PageResponse<RatingResponse> getAllRatings(
        @RequestParam(defaultValue = "0") @Min(0) Integer offset,
        @RequestParam(defaultValue = "10") @Min(1) @Max(100) Integer limit) {
        return ratingService.getAllRatings(offset, limit);
    }

    @GetMapping("/passengers/{passengerId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('PASSENGER')")
    @ValidateAccessPassenger
    public PageResponse<RatingResponse> getAllRatingsByPassenger(
        @PathVariable UUID passengerId,
        @RequestParam(defaultValue = "0") @Min(0) Integer offset,
        @RequestParam(defaultValue = "10") @Min(1) @Max(100) Integer limit,
        JwtAuthenticationToken jwt) {
        return ratingService.getAllRatingsByPassenger(offset, limit, passengerId);
    }

    @GetMapping("/drivers/{driverId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('DRIVER')")
    @ValidateAccessDriver
    public PageResponse<RatingResponse> getAllRatingsByDriver(
        @PathVariable UUID driverId,
        @RequestParam(defaultValue = "0") @Min(0) Integer offset,
        @RequestParam(defaultValue = "10") @Min(1) @Max(100) Integer limit,
        JwtAuthenticationToken jwt) {
        return ratingService.getAllRatingsByDriver(offset, limit, driverId);
    }

    @GetMapping("/passengers/{passengerId}/average-rating")
    @PreAuthorize("hasRole('ADMIN') or hasRole('PASSENGER')")
    @ValidateAccessPassenger
    public AverageRatingResponse getAverageRatingForPassenger(@PathVariable UUID passengerId,
                                                              JwtAuthenticationToken jwt) {
        String languageTag = LocaleContextHolder.getLocale().toLanguageTag();

        return ratingService.getAverageRatingForPassenger(passengerId, languageTag);
    }

    @GetMapping("/drivers/{driverId}/average-rating")
    @PreAuthorize("hasRole('ADMIN') or hasRole('DRIVER')")
    @ValidateAccessDriver
    public AverageRatingResponse getAverageRatingForDriver(@PathVariable UUID driverId,
                                                           JwtAuthenticationToken jwt) {
        String languageTag = LocaleContextHolder.getLocale().toLanguageTag();

        return ratingService.getAverageRatingForDriver(driverId, languageTag);
    }

    @PostMapping("/test/trigger-kafka")
    @PreAuthorize("hasRole('ADMIN')")
    public void triggerCalculateAndSendAverageRatings() {
        ratingService.calculateAndSendAverageRatings();
    }

}
