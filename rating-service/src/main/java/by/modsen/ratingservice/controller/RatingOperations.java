package by.modsen.ratingservice.controller;

import by.modsen.ratingservice.dto.request.RatingRequest;
import by.modsen.ratingservice.dto.response.AverageRatingResponse;
import by.modsen.ratingservice.dto.response.PageResponse;
import by.modsen.ratingservice.dto.response.RatingResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.util.UUID;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "Rating Controller", description = "This controller contains CRUD operations for ratings")
public interface RatingOperations {

    @Operation(description = "Creates a new rating from passenger/driver!")
    RatingResponse createRating(@RequestBody @Valid RatingRequest ratingRequest);

    @Operation(description = "Updates an existing rating!")
    RatingResponse updateRating(@RequestBody @Valid RatingRequest ratingRequest,
                                @PathVariable String ratingId);

    @Operation(description = "Deletes rating!")
    void deleteRating(@PathVariable String ratingId);

    @Operation(description = "Retrieving a rating by ID")
    RatingResponse getRatingById(@PathVariable String ratingId);

    @Operation(description = "Retrieving a page of all ratings")
    PageResponse<RatingResponse> getAllRatings(
        @RequestParam(defaultValue = "0") @Min(0) Integer offset,
        @RequestParam(defaultValue = "10") @Min(1) @Max(100) Integer limit);

    @Operation(description = "Retrieving a page of all ratings connected to passenger")
    PageResponse<RatingResponse> getAllRatingsByPassenger(
        @PathVariable UUID passengerId,
        @RequestParam(defaultValue = "0") @Min(0) Integer offset,
        @RequestParam(defaultValue = "10") @Min(1) @Max(100) Integer limit,
        JwtAuthenticationToken jwt);

    @Operation(description = "Retrieving a page of all ratings connected to driver")
    PageResponse<RatingResponse> getAllRatingsByDriver(
        @PathVariable UUID driverId,
        @RequestParam(defaultValue = "0") @Min(0) Integer offset,
        @RequestParam(defaultValue = "10") @Min(1) @Max(100) Integer limit,
        JwtAuthenticationToken jwt);

    @Operation(description = "Retrieving an average rating for passenger")
    AverageRatingResponse getAverageRatingForPassenger(@PathVariable UUID passengerId,
                                                       JwtAuthenticationToken jwt);

    @Operation(description = "Retrieving an average rating for driver")
    AverageRatingResponse getAverageRatingForDriver(@PathVariable UUID driverId,
                                                    JwtAuthenticationToken jwt);

}
