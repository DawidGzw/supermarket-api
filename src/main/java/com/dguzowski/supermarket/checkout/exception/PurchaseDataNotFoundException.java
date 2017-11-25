package com.dguzowski.supermarket.checkout.exception;

import com.dguzowski.supermarket.checkout.rest.model.PurchaseData;

import java.util.UUID;

public class PurchaseDataNotFoundException extends DataNotFoundException {

    public PurchaseDataNotFoundException(UUID purchaseId) {
        super("Purchease with specified identifier {0} was not found", PurchaseData.class, purchaseId);
    }
}
