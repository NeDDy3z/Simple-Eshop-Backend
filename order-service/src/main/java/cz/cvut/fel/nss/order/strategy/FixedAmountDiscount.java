package cz.cvut.fel.nss.order.strategy;

import lombok.AllArgsConstructor;
import java.math.BigDecimal;

@AllArgsConstructor
public class FixedAmountDiscount implements DiscountStrategy {
    private final BigDecimal amount;

    @Override
    public BigDecimal calculateDiscount(BigDecimal originalPrice) {
        BigDecimal result = originalPrice.subtract(amount);
        return result.compareTo(BigDecimal.ZERO) < 0 ? BigDecimal.ZERO : result;
    }
}
