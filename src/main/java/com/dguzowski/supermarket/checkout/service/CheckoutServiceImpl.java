package com.dguzowski.supermarket.checkout.service;

import com.dguzowski.supermarket.checkout.domain.Product;
import com.dguzowski.supermarket.checkout.domain.Purchase;
import com.dguzowski.supermarket.checkout.domain.PurchaseItem;
import com.dguzowski.supermarket.checkout.exception.ItemDataNotFoundInPurchaseException;
import com.dguzowski.supermarket.checkout.exception.ProductDataNotFoundException;
import com.dguzowski.supermarket.checkout.exception.PurchaseDataNotFoundException;
import com.dguzowski.supermarket.checkout.repositories.ProductRepository;
import com.dguzowski.supermarket.checkout.repositories.PurchaseRepository;
import com.dguzowski.supermarket.checkout.rest.model.PurchaseData;
import com.dguzowski.supermarket.checkout.rest.model.ScanningInfo;
import com.dguzowski.supermarket.checkout.rest.model.TotalPrice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;


@Service
@Transactional
public class CheckoutServiceImpl implements CheckoutService {

    private ProductRepository productRespository;
    private PurchaseRepository purchaseRepository;

    private Map<UUID, Purchase> pendingPurchases = new HashMap<>();

    @Autowired
    public CheckoutServiceImpl(ProductRepository productRespository, PurchaseRepository purchaseRepository) {
        this.productRespository = productRespository;
        this.purchaseRepository = purchaseRepository;
    }

    @Override
    public PurchaseData newPurchease(ScanningInfo scanData) {
        Optional<Product> productOpt = this.productRespository.findProductByBarcode(scanData.getBarcode());
        if (!productOpt.isPresent()) {
            throw new ProductDataNotFoundException(scanData.getBarcode());
        }
        Product product = productOpt.get();
        UUID purchaseId = UUID.randomUUID();
        Purchase purchase = new Purchase(purchaseId);
        PurchaseItem item = new PurchaseItem(product, purchase, scanData.getAmount());
        this.pendingPurchases.put(purchaseId, purchase);
        return new PurchaseData(purchase.getTotalPrice(), purchaseId);
    }

    @Override
    public TotalPrice addItemsToPurchase(UUID purchaseId, ScanningInfo scanData) {
        Purchase purchase = this.pendingPurchases.get(purchaseId);
        if(purchase == null){
            throw new PurchaseDataNotFoundException(purchaseId);
        }
        Optional<Product> product = this.productRespository.findProductByBarcode(scanData.getBarcode());
        if(!product.isPresent()){
            throw new ProductDataNotFoundException(scanData.getBarcode());
        }
        Optional<PurchaseItem> itemOpt = purchase.getPurchaseItemByProduct(product.get());
        if(!itemOpt.isPresent()){
            new PurchaseItem(product.get(), purchase, scanData.getAmount());
        }
        else {
            PurchaseItem item = itemOpt.get();
            item.changeAmount(scanData.getAmount());
        }
        return new TotalPrice(purchase.getTotalPrice());
    }

    @Override
    public void deletePurchase(UUID purchaseId){
        this.pendingPurchases.remove(purchaseId);
    }

    @Override
    public Optional<TotalPrice> deletePurchaseItem(UUID purchaseId, Optional<ScanningInfo> scanData) {
        TotalPrice totalPrice = null;
        if(!scanData.isPresent()){
            this.deletePurchase(purchaseId);
        }
        else{
            ScanningInfo scanInfo = scanData.get();
            Purchase purchase = this.pendingPurchases.get(purchaseId);
            if(purchase == null){
                throw new PurchaseDataNotFoundException(purchaseId);
            }
            Optional<Product> productOpt = this.productRespository.findProductByBarcode(scanInfo.getBarcode());
            if(!productOpt.isPresent()){
                throw new ProductDataNotFoundException(scanInfo.getBarcode());
            }
            Optional<PurchaseItem> item = purchase.getPurchaseItemByProduct(productOpt.get());
            if(!item.isPresent()){
                throw new ItemDataNotFoundInPurchaseException(purchaseId, scanInfo.getBarcode());
            }
            PurchaseItem purchaseItem = item.get();
            purchaseItem.changeAmount(-scanInfo.getAmount());
            totalPrice = new TotalPrice(purchase.getTotalPrice());
        }
        return Optional.ofNullable(totalPrice);
    }

    @Override
    public void savePurchase(UUID uniquePurchaseId) {
        Purchase purchase = this.pendingPurchases.get(uniquePurchaseId);
        if(purchase == null){
            throw new PurchaseDataNotFoundException(uniquePurchaseId);
        }
        this.purchaseRepository.save(purchase);
    }
}
