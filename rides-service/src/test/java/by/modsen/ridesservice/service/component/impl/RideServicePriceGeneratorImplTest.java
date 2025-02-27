package by.modsen.ridesservice.service.component.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class RideServicePriceGeneratorImplTest {

    @InjectMocks
    private RideServicePriceGeneratorImpl rideServicePriceGenerator;

    @Test
    void generateRandomCost_ReturnsBigDecimal() {
        BigDecimal result = rideServicePriceGenerator.generateRandomCost();

        assertThat(result).isInstanceOf(BigDecimal.class);
    }

    @Test
    void generateRandomCost_ReturnsValueInRange() {
        BigDecimal result = rideServicePriceGenerator.generateRandomCost();

        assertThat(result)
            .isGreaterThanOrEqualTo(BigDecimal.ZERO)
            .isLessThanOrEqualTo(new BigDecimal("99.99"));
    }

    @Test
    void generateRandomCost_ReturnsValueWithTwoDecimalPlaces() {
        BigDecimal result = rideServicePriceGenerator.generateRandomCost();

        assertThat(result.scale()).isEqualTo(2);
    }

    @Test
    void generateRandomCost_ReturnsDifferentValuesOnMultipleCalls() {
        BigDecimal firstCall = rideServicePriceGenerator.generateRandomCost();
        BigDecimal secondCall = rideServicePriceGenerator.generateRandomCost();

        assertThat(firstCall).isNotEqualTo(secondCall);
    }

    @Test
    void generateRandomCost_ReturnsMinimumValue() {
        BigDecimal minValue = BigDecimal.ZERO.setScale(2);

        BigDecimal result = rideServicePriceGenerator.generateRandomCost();

        assertThat(result).isGreaterThanOrEqualTo(minValue);
    }

    @Test
    void generateRandomCost_ReturnsMaximumValue() {
        BigDecimal maxValue = new BigDecimal("99.99");

        BigDecimal result = rideServicePriceGenerator.generateRandomCost();

        assertThat(result).isLessThanOrEqualTo(maxValue);
    }

}