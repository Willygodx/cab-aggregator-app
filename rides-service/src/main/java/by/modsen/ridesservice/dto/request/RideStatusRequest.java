package by.modsen.ridesservice.dto.request;

import by.modsen.ridesservice.dto.Marker;
import by.modsen.ridesservice.model.enums.ValidRideStatus;
import jakarta.validation.constraints.NotBlank;

public record RideStatusRequest(

    @ValidRideStatus(groups = {Marker.OnCreate.class, Marker.OnUpdate.class})
    @NotBlank(groups = {Marker.OnCreate.class}, message = "{ride_status.blank.message}")
    String rideStatus

) {
}
