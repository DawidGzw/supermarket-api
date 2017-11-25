package com.dguzowski.supermarket.checkout.service;

import com.dguzowski.supermarket.checkout.exception.ItemDataNotFoundInPurchaseException;
import com.dguzowski.supermarket.checkout.exception.PurchaseDataNotFoundException;
import com.dguzowski.supermarket.checkout.rest.model.PurchaseData;
import com.dguzowski.supermarket.checkout.rest.model.ScanningInfo;
import com.dguzowski.supermarket.checkout.rest.model.TotalPrice;

import java.util.Optional;
import java.util.UUID;

public interface CheckoutService {

    PurchaseData newPurchease(ScanningInfo scanData);

    TotalPrice addItemsToPurchase(UUID purchaseId, ScanningInfo scanData) throws PurchaseDataNotFoundException;

    void deletePurchase(UUID purchaseId) throws PurchaseDataNotFoundException;

    Optional<TotalPrice> deletePurchaseItem(UUID purchaseId, Optional<ScanningInfo> scanData) throws PurchaseDataNotFoundException, ItemDataNotFoundInPurchaseException;

    void savePurchase(UUID uniquePurchaseId);
}
