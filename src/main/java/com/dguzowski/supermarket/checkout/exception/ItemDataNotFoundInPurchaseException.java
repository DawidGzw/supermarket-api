package com.dguzowski.supermarket.checkout.exception;

import com.dguzowski.supermarket.checkout.rest.model.PurchaseData;

import java.util.UUID;

public class ItemDataNotFoundInPurchaseException extends DataNotFoundException{

    public ItemDataNotFoundInPurchaseException(UUID purchaseId, String barcode){
        super("Specified item {0} not found in purchase {1}", PurchaseData.class, purchaseId, barcode);
    }
}
