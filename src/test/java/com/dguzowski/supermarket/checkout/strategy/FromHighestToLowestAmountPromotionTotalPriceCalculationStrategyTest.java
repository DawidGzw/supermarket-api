package com.dguzowski.supermarket.checkout.strategy;

import com.dguzowski.supermarket.checkout.domain.Product;
import com.dguzowski.supermarket.checkout.domain.Promotion;
import org.junit.Before;
import org.junit.Test;
import org.hamcrest.Matchers;
import java.math.BigDecimal;
import java.util.Set;

import static org.junit.Assert.*;

public class FromHighestToLowestAmountPromotionTotalPriceCalculationStrategyTest {


    private TotalPriceCalculationStrategy calculationStrategy = new FromHighestToLowestAmountPromotionTotalPriceCalculationStrategy();

    private Product product = new Product("12345678", "spyProduct", new BigDecimal("12.50"));

    @Before
    public void setUp(){
       new Promotion(this.product, 2, new BigDecimal("22.00")); // 11.00 per piece
       new Promotion(this.product, 3, new BigDecimal("30.00")); // 10.00 per piece
       new Promotion(this.product, 4, new BigDecimal("36.00")); // 9.00 per piece
       new Promotion(this.product, 5, new BigDecimal("40.00")); // 8.00 per piece
    }

    @Test
    public void calculateTotalPrice() throws Exception {
        //testing for one product
        int testedAmount = 1;
        BigDecimal expectedPrice = new BigDecimal("12.50");
        this.testAmount(testedAmount, expectedPrice);

        testedAmount = 2;
        expectedPrice = new BigDecimal("22.00");
        this.testAmount(testedAmount, expectedPrice);

        testedAmount = 3;
        expectedPrice = new BigDecimal("30.00");
        this.testAmount(testedAmount, expectedPrice);

        testedAmount = 4;
        expectedPrice = new BigDecimal("36.00");
        this.testAmount(testedAmount, expectedPrice);

        testedAmount = 5;
        expectedPrice = new BigDecimal("40.00");
        this.testAmount(testedAmount, expectedPrice);

        testedAmount = 6;
        expectedPrice = new BigDecimal("52.50");
        this.testAmount(testedAmount, expectedPrice);

        testedAmount = 7;
        expectedPrice = new BigDecimal("62.00");
        this.testAmount(testedAmount, expectedPrice);

        testedAmount = 8;
        expectedPrice = new BigDecimal("70.00");
        this.testAmount(testedAmount, expectedPrice);

        testedAmount = 9;
        expectedPrice = new BigDecimal("76.00");
        this.testAmount(testedAmount, expectedPrice);

        testedAmount = 10;
        expectedPrice = new BigDecimal("80.00");
        this.testAmount(testedAmount, expectedPrice);

    }

    private void testAmount(int amount, BigDecimal expectedValue){
        BigDecimal calculatedPrice = this.calculationStrategy.calculateTotalPrice(this.product, amount);
        assertThat(calculatedPrice, Matchers.equalTo(expectedValue));
    }

}