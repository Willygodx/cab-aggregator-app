package by.modsen.driverservice.dto.request;

import by.modsen.driverservice.constants.ApplicationConstants;
import by.modsen.driverservice.dto.Marker;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

@Schema(description = "Data Transfer Object for Car")
public record CarRequest(

    @NotBlank(groups = {Marker.OnCreate.class}, message = "{car_color.blank.message}")
    @Schema(description = "Car's color", example = "Yellow")
    String color,

    @NotBlank(groups = {Marker.OnCreate.class}, message = "{car_brand.blank.message}")
    @Schema(description = "Car brand", example = "Bmw")
    String carBrand,

    @Pattern(groups = {Marker.OnCreate.class, Marker.OnUpdate.class},
        regexp = ApplicationConstants.CAR_NUMBER_REGEXP,
        message = "{car_number.invalid.message}")
    @NotBlank(groups = {Marker.OnCreate.class}, message = "{car_number.blank.message}")
    @Schema(description = "Car's number", example = "ABC123")
    String carNumber

) {
}

