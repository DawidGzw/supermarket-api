package com.dguzowski.supermarket.checkout.strategy;

import com.dguzowski.supermarket.checkout.domain.Product;

import java.math.BigDecimal;

public interface TotalPriceCalculationStrategy {
    BigDecimal calculateTotalPrice(Product product, int amount);
}
