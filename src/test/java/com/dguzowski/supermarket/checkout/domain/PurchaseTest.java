package com.dguzowski.supermarket.checkout.domain;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class PurchaseTest {

    @Spy
    private Purchase purchaseUnderTest = new Purchase(UUID.randomUUID());


    @Test
    public void addItem() throws Exception {
        PurchaseItem item = this.addItemToPurchase();
        assertThat(this.purchaseUnderTest.getItems(), org.hamcrest.Matchers.hasSize(1));
        assertThat(this.purchaseUnderTest.getItems().iterator().next(), org.hamcrest.Matchers.equalTo(item));
    }

    @Test
    public void removeItem() throws Exception {
        PurchaseItem item = this.addItemToPurchase();
        this.purchaseUnderTest.removeItem(item);
        assertThat( this.purchaseUnderTest.getItems(), org.hamcrest.Matchers.empty());
    }


    private PurchaseItem addItemToPurchase(){
        PurchaseItem item = new PurchaseItem();

        //setting id for hashCode method
        PurchaseItem.Id id= new PurchaseItem.Id(UUID.randomUUID(), 1L);
        ReflectionTestUtils.setField(item, "id", id);
        ReflectionTestUtils.setField(item, "totalPrice", new BigDecimal("10.00"));

        Mockito.doNothing().when(this.purchaseUnderTest).changeTotalPrice(Matchers.any(BigDecimal.class));
        this.purchaseUnderTest.addItem(item);
        return item;
    }


    @Test
    public void changeTotalPrice() throws Exception {
        ReflectionTestUtils.setField(this.purchaseUnderTest, "totalPrice", new BigDecimal("10.00"));
        this.purchaseUnderTest.changeTotalPrice(new BigDecimal("5.25"));
        assertThat(this.purchaseUnderTest.getTotalPrice(), org.hamcrest.Matchers.equalTo(new BigDecimal("15.25")));
    }

    @Test
    public void getPurchaseItemByProduct() throws Exception {
        PurchaseItem item = this.addItemToPurchase();
        Product p = new Product("12345678", "prod1", new BigDecimal("12.05"));
        ReflectionTestUtils.setField(item, "product", p);
        assertThat(this.purchaseUnderTest.getPurchaseItemByProduct(p).get(), org.hamcrest.Matchers.equalTo(item));
    }

}