package by.modsen.ratingservice.controller.impl;

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
import lombok.RequiredArgsConstructor;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
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
    public RatingResponse createRating(@RequestBody @Valid RatingRequest ratingRequest) {
        String languageTag = LocaleContextHolder.getLocale().toLanguageTag();

        return ratingService.createRating(ratingRequest, languageTag);
    }

    @PutMapping("/{ratingId}")
    @Validated({Marker.OnUpdate.class})
    public RatingResponse updateRating(@RequestBody @Valid RatingRequest ratingRequest,
                                       @PathVariable String ratingId) {
        return ratingService.updateRating(ratingRequest, ratingId);
    }

    @DeleteMapping("/{ratingId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
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
    public PageResponse<RatingResponse> getAllRatingsByPassenger(
        @RequestParam(defaultValue = "0") @Min(0) Integer offset,
        @RequestParam(defaultValue = "10") @Min(1) @Max(100) Integer limit,
        @PathVariable Long passengerId) {
        return ratingService.getAllRatingsByPassenger(offset, limit, passengerId);
    }

    @GetMapping("/drivers/{driverId}")
    public PageResponse<RatingResponse> getAllRatingsByDriver(
        @RequestParam(defaultValue = "0") @Min(0) Integer offset,
        @RequestParam(defaultValue = "10") @Min(1) @Max(100) Integer limit,
        @PathVariable Long driverId) {
        return ratingService.getAllRatingsByDriver(offset, limit, driverId);
    }

    @GetMapping("/passengers/{passengerId}/average-rating")
    public AverageRatingResponse getAverageRatingForPassenger(@PathVariable Long passengerId) {
        String languageTag = LocaleContextHolder.getLocale().toLanguageTag();

        return ratingService.getAverageRatingForPassenger(passengerId, languageTag);
    }

    @GetMapping("/drivers/{driverId}/average-rating")
    public AverageRatingResponse getAverageRatingForDriver(@PathVariable Long driverId) {
        String languageTag = LocaleContextHolder.getLocale().toLanguageTag();

        return ratingService.getAverageRatingForDriver(driverId, languageTag);
    }

}
