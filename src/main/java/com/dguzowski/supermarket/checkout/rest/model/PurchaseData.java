package com.dguzowski.supermarket.checkout.rest.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.util.UUID;

public class PurchaseData extends TotalPrice {

    public PurchaseData(){}

    public PurchaseData(BigDecimal totalPrice, UUID purchaseId) {
        super(totalPrice);
        this.purchaseId = purchaseId;
    }

    @JsonProperty("purchase_id")
    private UUID purchaseId;

    public UUID getPurchaseId() {
        return purchaseId;
    }

    public void setPurchaseId(UUID purchaseId) {
        this.purchaseId = purchaseId;
    }
}
