package cz.cvut.fel.nss.order.strategy;

import lombok.AllArgsConstructor;
import java.math.BigDecimal;
import java.math.RoundingMode;

@AllArgsConstructor
public class PercentageDiscount implements DiscountStrategy {
    private final double percentage;

    @Override
    public BigDecimal calculateDiscount(BigDecimal originalPrice) {
        BigDecimal multiplier = BigDecimal.valueOf(1 - percentage / 100.0);
        return originalPrice.multiply(multiplier).setScale(2, RoundingMode.HALF_UP);
    }
}
