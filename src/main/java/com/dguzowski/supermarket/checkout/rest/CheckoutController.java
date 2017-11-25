package com.dguzowski.supermarket.checkout.rest;


import com.dguzowski.supermarket.checkout.exception.DataNotFoundException;
import com.dguzowski.supermarket.checkout.rest.model.PurchaseData;
import com.dguzowski.supermarket.checkout.rest.model.ScanningInfo;
import com.dguzowski.supermarket.checkout.rest.model.TotalPrice;
import com.dguzowski.supermarket.checkout.service.CheckoutService;
import com.dguzowski.supermarket.checkout.util.HeaderUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Optional;
import java.util.UUID;

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
    @PostMapping( value="/{purchaseId}", consumes = {MediaType.APPLICATION_JSON_UTF8_VALUE}, produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
    public ResponseEntity<TotalPrice> addItemsToPurchase(@PathVariable UUID purchaseId , @RequestBody @Valid ScanningInfo scanData){
        TotalPrice totalPrice = this.checkoutService.addItemsToPurchase(purchaseId, scanData);
        return ResponseEntity.ok(totalPrice);
    }

    @DeleteMapping("/{purchaseId}")
    public ResponseEntity<?> removePurchase(@PathVariable UUID purchaseId, @RequestBody(required = false) @Valid ScanningInfo scanData){
        Optional<TotalPrice> totalPrice = this.checkoutService.deletePurchaseItem(purchaseId, Optional.ofNullable(scanData));
        ResponseEntity<?> result = totalPrice.map( price -> ResponseEntity.ok(price))
                .orElse(ResponseEntity.ok().build());
        return result;
    }

    @GetMapping("/finish/{purchaseId}")
    public ResponseEntity<Void> savePurchaseData(@PathVariable UUID purchaseId){
        this.checkoutService.savePurchase(purchaseId);
        return ResponseEntity.ok().build();
    }


    @ExceptionHandler({DataNotFoundException.class})
    public ResponseEntity<Void> handleDataNotFoundException(DataNotFoundException exception){
        HttpHeaders headers = HeaderUtil.createAlert(exception.getMessage(), StringUtils.arrayToCommaDelimitedString(exception.getParams()));
        return ResponseEntity.notFound().headers(headers).build();
    };


}
