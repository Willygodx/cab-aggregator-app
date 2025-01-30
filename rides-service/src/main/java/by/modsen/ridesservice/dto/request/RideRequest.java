package by.modsen.ridesservice.dto.request;

import by.modsen.ridesservice.dto.Marker;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;

public record RideRequest(

    @Null(groups = {Marker.OnUpdate.class}, message = "{driver_id.null.message}")
    @NotNull(groups = {Marker.OnCreate.class}, message = "{driver_id.blank.message}")
    Long driverId,

    @Null(groups = {Marker.OnUpdate.class}, message = "{passenger_id.null.message}")
    @NotNull(groups = {Marker.OnCreate.class}, message = "{passenger_id.blank.message}")
    Long passengerId,

    @Max(value = 128, groups = {Marker.OnCreate.class, Marker.OnUpdate.class}, message = "{pickup_address.invalid.value.message}")
    @NotBlank(groups = {Marker.OnCreate.class}, message = "{pickup_address.blank.message}")
    String pickupAddress,

    @Max(value = 128, groups = {Marker.OnCreate.class, Marker.OnUpdate.class}, message = "{destination_address.invalid.value.message}")
    @NotBlank(groups = {Marker.OnCreate.class}, message = "{destination_address.blank.message}")
    String destinationAddress

) {
}
