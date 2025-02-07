package by.modsen.ridesservice.service.component.impl;

import by.modsen.ridesservice.service.component.RideServicePriceGenerator;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.SecureRandom;
import org.springframework.stereotype.Component;

@Component
public class RideServicePriceGeneratorImpl implements RideServicePriceGenerator {

    private static final int SCALE = 2;
    private static final int MAX = 99;

    public BigDecimal generateRandomCost() {
        SecureRandom secureRandom = new SecureRandom();

        int wholePart = secureRandom.nextInt(MAX + 1);
        int decimalPart = secureRandom.nextInt(100);

        String costString = String.format("%d.%02d", wholePart, decimalPart);

        return new BigDecimal(costString).setScale(SCALE, RoundingMode.HALF_UP);
    }

}
