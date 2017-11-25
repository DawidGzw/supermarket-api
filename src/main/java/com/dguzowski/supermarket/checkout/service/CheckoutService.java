package com.dguzowski.supermarket.checkout.service;

import com.dguzowski.supermarket.checkout.exception.PurchaseNotFoundException;
import com.dguzowski.supermarket.checkout.model.PurchaseData;
import com.dguzowski.supermarket.checkout.model.ScanningInfo;
import com.dguzowski.supermarket.checkout.model.TotalPrice;
import javassist.NotFoundException;

import java.util.UUID;

public interface CheckoutService {

    PurchaseData newPurchease(ScanningInfo scanData);

    TotalPrice addItemsToPurchase(UUID purchaseId, ScanningInfo any) throws PurchaseNotFoundException;

}
