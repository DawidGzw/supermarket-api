package com.dguzowski.supermarket.checkout.service;

import com.dguzowski.supermarket.checkout.exception.ItemDataNotFoundInPurchaseException;
import com.dguzowski.supermarket.checkout.exception.PurchaseDataNotFoundException;
import com.dguzowski.supermarket.checkout.rest.model.PurchaseData;
import com.dguzowski.supermarket.checkout.rest.model.ScanningInfo;
import com.dguzowski.supermarket.checkout.rest.model.TotalPrice;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;
import java.util.UUID;


@Service
@Transactional
public class CheckoutServiceImpl implements CheckoutService {
    @Override
    public PurchaseData newPurchease(ScanningInfo scanData) {
        return null;
    }

    @Override
    public TotalPrice addItemsToPurchase(UUID purchaseId, ScanningInfo any) throws PurchaseDataNotFoundException {
        return null;
    }

    @Override
    public void deletePurchase(UUID purchaseId) throws PurchaseDataNotFoundException{

    }

    @Override
    public Optional<TotalPrice> deletePurchaseItem(UUID purchaseId, Optional<ScanningInfo> scanData) throws PurchaseDataNotFoundException, ItemDataNotFoundInPurchaseException{
        return null;
    }

    @Override
    public void savePurchase(UUID uniquePurchaseId) throws PurchaseDataNotFoundException{

    }
}
