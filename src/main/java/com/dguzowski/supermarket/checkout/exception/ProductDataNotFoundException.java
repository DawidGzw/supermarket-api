package com.dguzowski.supermarket.checkout.exception;

import com.dguzowski.supermarket.checkout.domain.Product;

public class ProductDataNotFoundException extends DataNotFoundException {

    public ProductDataNotFoundException(String barcode) {
        super("Poduct with specified barcode {0} was not found", Product.class, barcode);
    }
}
