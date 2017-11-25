package com.dguzowski.supermarket.checkout.model;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

//package scope to allow unit testing
public class ScanningInfo {
    @Pattern(message = "Invalid barcode pattern", regexp = "^[0-9]{8}")
    @NotNull
    private String barcode;

    public ScanningInfo(){};

    public ScanningInfo(String barcode, Integer amount){
        this.barcode = barcode;
        this.amount = amount;
    };

    @NotNull
    @Min(message="Unexpected amount of products provided", value = 1)
    private Integer amount;

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }
}
