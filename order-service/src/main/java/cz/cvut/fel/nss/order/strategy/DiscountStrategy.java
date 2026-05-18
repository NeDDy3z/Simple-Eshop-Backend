package cz.cvut.fel.nss.order.strategy;

import java.math.BigDecimal;

public interface DiscountStrategy {
    BigDecimal calculateDiscount(BigDecimal originalPrice);
}
