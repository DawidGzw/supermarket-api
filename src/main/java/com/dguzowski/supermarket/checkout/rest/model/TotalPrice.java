package com.dguzowski.supermarket.checkout.rest.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public class TotalPrice {

    public TotalPrice() {
    }

    public TotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    @JsonProperty("total_price")
    private BigDecimal totalPrice;

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }
}
