package by.modsen.ridesservice.dto.request;

import by.modsen.ridesservice.dto.Marker;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import java.util.UUID;

public record RideRequest(

    @Null(groups = {Marker.OnUpdate.class}, message = "{driver_id.null.message}")
    @NotNull(groups = {Marker.OnCreate.class}, message = "{driver_id.blank.message}")
    UUID driverId,

    @Null(groups = {Marker.OnUpdate.class}, message = "{passenger_id.null.message}")
    @NotNull(groups = {Marker.OnCreate.class}, message = "{passenger_id.blank.message}")
    UUID passengerId,

    @NotBlank(groups = {Marker.OnCreate.class}, message = "{pickup_address.blank.message}")
    String pickupAddress,

    @NotBlank(groups = {Marker.OnCreate.class}, message = "{destination_address.blank.message}")
    String destinationAddress

) {
}
