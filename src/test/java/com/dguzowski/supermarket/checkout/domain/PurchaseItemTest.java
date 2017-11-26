package com.dguzowski.supermarket.checkout.domain;

import com.dguzowski.supermarket.checkout.strategy.TotalPriceCalculationStrategy;
import com.dguzowski.supermarket.checkout.strategy.TotalPriceCalculationStrategyProvider;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;

@RunWith(MockitoJUnitRunner.class)
public class PurchaseItemTest {

    @Mock
    private TotalPriceCalculationStrategy strategy;

    private TotalPriceCalculationStrategyProvider provider=new TotalPriceCalculationStrategyProvider("highest_to_lowest_amount");

    @Spy
    private Purchase purchase = new Purchase(UUID.randomUUID());

    @Spy
    private Product product = new Product("12345678", "name", new BigDecimal("10.00"));

    @Spy
    private PurchaseItem purchaseItemUnderTest = new PurchaseItem();

    @Before
    public void setUp(){
        ReflectionTestUtils.setField(this.purchaseItemUnderTest, "amount", 1);
        ReflectionTestUtils.setField(this.purchaseItemUnderTest, "totalPrice", new BigDecimal("10.00"));
        ReflectionTestUtils.setField(this.purchaseItemUnderTest, "purchase", this.purchase);
        ReflectionTestUtils.setField(this.purchaseItemUnderTest, "product", this.product);
        PurchaseItem.Id id= new PurchaseItem.Id(this.purchase.getId(), this.product.getId());
        ReflectionTestUtils.setField(this.purchaseItemUnderTest, "id", id);
        ReflectionTestUtils.setField(this.provider, "currentTotalPriceCalculationStrategy", this.strategy);
    }

    @Test
    public void changeAmount() throws Exception {
        Mockito.doNothing().when(purchaseItemUnderTest).calculateTotalPrice();
        this.purchaseItemUnderTest.changeAmount(1);
        Mockito.verify(this.purchase).changeTotalPrice(any(BigDecimal.class));
        assertThat(purchaseItemUnderTest.getAmount(), Matchers.equalTo(2));
    }

    @Test
    public void removeMoreAmountThanIsPresent() throws Exception {
        Mockito.doNothing().when(purchaseItemUnderTest).calculateTotalPrice();
        this.purchaseItemUnderTest.changeAmount(-10);
        Mockito.verify(this.purchase, Mockito.times(2)).changeTotalPrice(any(BigDecimal.class));
        assertThat(purchaseItemUnderTest.getAmount(), Matchers.equalTo(0));
    }

    @Test
    public void calculateTotalPrice() throws Exception {
        Mockito.when(this.strategy.calculateTotalPrice(this.product, 1))
                .thenReturn(new BigDecimal("20.00"));

        this.purchaseItemUnderTest.calculateTotalPrice();
        assertThat(this.purchaseItemUnderTest.getTotalPrice(), Matchers.equalTo(new BigDecimal("20.00")));
    }

}