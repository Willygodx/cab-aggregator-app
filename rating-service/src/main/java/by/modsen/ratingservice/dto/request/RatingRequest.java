package by.modsen.ratingservice.dto.request;

import by.modsen.ratingservice.dto.Marker;
import by.modsen.ratingservice.model.enums.ValidRatedBy;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.Size;

public record RatingRequest(

    @NotNull(groups = Marker.OnCreate.class, message = "{ride_id.not.null.message}")
    @Null(groups = Marker.OnUpdate.class, message = "{ride_id.null.message}")
    Long rideId,

    @Min(value = 1, groups = {Marker.OnCreate.class, Marker.OnUpdate.class}, message = "{mark.invalid.value.message}")
    @Max(value = 5, groups = {Marker.OnCreate.class, Marker.OnUpdate.class}, message = "{mark.invalid.value.message}")
    @NotNull(groups = Marker.OnCreate.class, message = "{mark.null.message}")
    Integer mark,

    @Size(max = 200, groups = {
        Marker.OnCreate.class,
        Marker.OnUpdate.class
    }, message = "{comment.invalid.size.message}")
    String comment

) {
}
