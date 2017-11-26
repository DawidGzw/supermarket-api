package com.dguzowski.supermarket.checkout.strategy;

import com.dguzowski.supermarket.checkout.domain.Product;
import com.dguzowski.supermarket.checkout.domain.Promotion;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.Set;

public class FromHighestToLowestAmountPromotionTotalPriceCalculationStrategy implements TotalPriceCalculationStrategy {
    @Override
    public BigDecimal calculateTotalPrice(Product product, int amount) {
        BigDecimal totalCost = new BigDecimal("0.00");
        while (amount > 0) {
            Set<Promotion> promotions = product.getApplicablePromotions(amount);
            Optional<Promotion> highestAmountPromotion = promotions.stream().max((promo1, promo2) -> promo1.getAmount().compareTo(promo2.getAmount()));
            if(highestAmountPromotion.isPresent()){
                Promotion promo = highestAmountPromotion.get();
                amount -= promo.getAmount();
                totalCost = totalCost.add(promo.getPrice());
            }
            else{
                totalCost = totalCost.add(product.getPrice().multiply(BigDecimal.valueOf(amount)));
                amount=0;
            }
        }
        return totalCost;
    }
}
