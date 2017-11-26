package com.dguzowski.supermarket.checkout.strategy;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Component
public class TotalPriceCalculationStrategyProvider {

    private static final Map<String, TotalPriceCalculationStrategy> calculationStrategies =
            new HashMap<String, TotalPriceCalculationStrategy>(){{
                this.put("highest_to_lowest_amount", new FromHighestToLowestAmountPromotionTotalPriceCalculationStrategy());
            }};

    private static TotalPriceCalculationStrategyProvider instance;

    private TotalPriceCalculationStrategy currentTotalPriceCalculationStrategy;

    public TotalPriceCalculationStrategyProvider(@Value("${application.cost.strategy:highest_to_lowest_amount}") String strategyName){
        TotalPriceCalculationStrategy calcStrategy = calculationStrategies.get(strategyName);
        Objects.requireNonNull(calcStrategy);
        this.currentTotalPriceCalculationStrategy=calcStrategy;
        instance = this;
    }

    public static TotalPriceCalculationStrategy getCalculationStrategy(){
        return instance.currentTotalPriceCalculationStrategy;
    }
}
