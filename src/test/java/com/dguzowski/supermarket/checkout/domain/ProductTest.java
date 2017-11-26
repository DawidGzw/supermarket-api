package com.dguzowski.supermarket.checkout.domain;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Set;

import static org.junit.Assert.*;

public class ProductTest {

    Product product = new Product("12345678", "ProductUnderTest", new BigDecimal("13.50"));

    @Before
    public void setUp(){
            new Promotion(this.product, 10, new BigDecimal("99.99"));
            new Promotion(this.product, 2, new BigDecimal("24.00"));
    }

    @Test
    public void getApplicablePromotions() throws Exception {

        int amountoOfProducts=1;
        int expectedApplicablePromotions=0;
        this.testAmountAndPromotions(amountoOfProducts, expectedApplicablePromotions);

        amountoOfProducts=9;
        expectedApplicablePromotions=1;
        this.testAmountAndPromotions(amountoOfProducts, expectedApplicablePromotions);

        amountoOfProducts=11;
        expectedApplicablePromotions=2;
        this.testAmountAndPromotions(amountoOfProducts, expectedApplicablePromotions);
    }

    private void testAmountAndPromotions(int amount, int expectedApplicablePromotions){
        Set<Promotion> applicablePromotions = this.product.getApplicablePromotions(amount);
        assertThat(applicablePromotions, Matchers.hasSize(expectedApplicablePromotions));
    }

}