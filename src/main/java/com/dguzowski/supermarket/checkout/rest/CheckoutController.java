package com.dguzowski.supermarket.checkout.rest;


import com.dguzowski.supermarket.checkout.exception.PurchaseNotFoundException;
import com.dguzowski.supermarket.checkout.model.PurchaseData;
import com.dguzowski.supermarket.checkout.model.ScanningInfo;
import com.dguzowski.supermarket.checkout.model.TotalPrice;
import com.dguzowski.supermarket.checkout.service.CheckoutService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import java.util.Optional;
import java.util.UUID;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8;

@RestController
@RequestMapping("/api/purchase")
public class CheckoutController {

    @Autowired
    public CheckoutController(CheckoutService checkoutService) {
        this.checkoutService = checkoutService;
    }

    private CheckoutService checkoutService;


    @PostMapping(consumes = {MediaType.APPLICATION_JSON_UTF8_VALUE}, produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
    public ResponseEntity<PurchaseData> intitiatiePurchease(@RequestBody @Valid ScanningInfo scanData){
        PurchaseData data = this.checkoutService.newPurchease(scanData);
        return ResponseEntity.ok(data);
    }

    //becouse i saw that, if i had more than one product, then before scanning shop assistant
    //pressed some buttons (probably amount) and scanned only one product.
    @PostMapping("/{purchase_id}")
    public ResponseEntity<TotalPrice> addItemsToPurchase(@PathVariable("purchase_id") UUID purchaseId , @RequestBody @Valid ScanningInfo scanData){
        TotalPrice totalPrice = this.checkoutService.addItemsToPurchase(purchaseId, scanData);
        return ResponseEntity.ok(totalPrice);
    }

    @DeleteMapping("/{purchase_id}")
    public ResponseEntity<Void> removePurchase(@PathVariable UUID purchaseId){
        return null;
    }

    @DeleteMapping("/{purchase_id}/{barcode}")
    public ResponseEntity<Void> removeItem(@PathVariable("purchase_id") UUID purchaseId, @PathVariable("barcode") String barcode){
        return null;
    }

    @DeleteMapping("/{purchase_id}/{barcode}/{amount}")
    public ResponseEntity<Void> removeItem(@PathVariable("purchase_id") UUID purchaseId,
                                                   @PathVariable("barcode") String barcode,
                                                   @PathVariable("amount") int amount){
        return null;
    }

    @GetMapping("/finish/{purchase_id}")
    public ResponseEntity<Void> savePurchaseData(@PathVariable UUID purchaseId){
        return null;
    }


    @ExceptionHandler({PurchaseNotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public void handlePurchaseNotFoundException(){};


}
