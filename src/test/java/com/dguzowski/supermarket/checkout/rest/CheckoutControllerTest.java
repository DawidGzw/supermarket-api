package com.dguzowski.supermarket.checkout.rest;

import com.dguzowski.supermarket.checkout.exception.PurchaseNotFoundException;
import com.dguzowski.supermarket.checkout.model.PurchaseData;
import com.dguzowski.supermarket.checkout.model.ScanningInfo;
import com.dguzowski.supermarket.checkout.model.TotalPrice;
import com.dguzowski.supermarket.checkout.service.CheckoutService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import java.math.BigDecimal;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

@RunWith(SpringRunner.class)
@WebMvcTest
public class CheckoutControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper mapper = new ObjectMapper();

    @MockBean
    private CheckoutService checkoutService;

    @Test
    public void whenNewScanIsPerformedAndParamsAreValidThenReturnPurchaseDataAndOK() throws Exception {
        String validBarcode = "12345678";
        ScanningInfo scanningInfo = new ScanningInfo(validBarcode, 1);
        String data = mapper.writeValueAsString(scanningInfo);

        UUID uniquePuechaseId = UUID.randomUUID();
        BigDecimal totalPrice = new BigDecimal("12.50");

        when(this.checkoutService.newPurchease(any())).thenReturn(new PurchaseData(totalPrice, uniquePuechaseId));

        this.mockMvc.perform(post("/api/purchase").content(data).contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk())
                //.andExpect(content().json(this.mapper.writeValueAsString(new PurchaseData(totalPrice, uniquePuechaseId))));
                // It defeated me. jsonPath method tries to check equality of Double and BigDecimal
                .andExpect(jsonPath("$.total_price", is(totalPrice.doubleValue())))
                .andExpect(jsonPath("$.purchase_id", is(uniquePuechaseId.toString())));
    }

    @Test
    public void whenNewScanIsPerformedAndBarcodeIsInvalidThenReturnClientErrorStatusCode() throws Exception {
        String invalidBarcode = "123456789";
        ScanningInfo scanningInfo = new ScanningInfo(invalidBarcode, 1);
        String data = mapper.writeValueAsString(scanningInfo);

        UUID uniquePuechaseId = UUID.randomUUID();
        BigDecimal totalPrice = new BigDecimal("12.50");

        when(this.checkoutService.newPurchease(any())).thenReturn(new PurchaseData(totalPrice, uniquePuechaseId));
        this.mockMvc.perform(post("/api/purchase").content(data).contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().is4xxClientError())
                .andExpect(content().bytes( new byte[0]));
    }

    @Test
    public void whenNewScanIsPerformedAndAmountIsInvalidThenReturnClientErrorStatusCode() throws Exception {
        String validBarcode = "12345678";
        ScanningInfo scanningInfo = new ScanningInfo(validBarcode, 0);
        String data = mapper.writeValueAsString(scanningInfo);

        UUID uniquePuechaseId = UUID.randomUUID();
        BigDecimal totalPrice = new BigDecimal("12.50");

        when(this.checkoutService.newPurchease(any())).thenReturn(new PurchaseData(totalPrice, uniquePuechaseId));
        this.mockMvc.perform(post("/api/purchase").content(data).contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().is4xxClientError())
                .andExpect(content().bytes( new byte[0]));
    }

    @Test
    public void whenProductIsAddedToPurchaseAndScanDataIsValidThenReturnTotalPriceAndOk() throws Exception {
        String validBarcode = "12345678";
        ScanningInfo scanningInfo = new ScanningInfo(validBarcode, 1);
        String data = mapper.writeValueAsString(scanningInfo);

        UUID uniquePurchaseId = UUID.randomUUID();
        BigDecimal totalPrice = new BigDecimal("12.50");

        when(this.checkoutService.addItemsToPurchase(eq(uniquePurchaseId), org.mockito.Matchers.any(ScanningInfo.class))).thenReturn(new TotalPrice(totalPrice));

        this.mockMvc.perform(post("/api/purchase/"+uniquePurchaseId.toString()).content(data).contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk())
                //.andExpect(content().json(this.mapper.writeValueAsString(new PurchaseData(totalPrice, uniquePuechaseId))));
                // It defeated me. jsonPath method tries to check equality of Double and BigDecimal
                .andExpect(jsonPath("$.total_price", is(totalPrice.doubleValue())));
    }

    @Test
    public void whenProductIsAddedToPurchaseAndScanDataIsInvalidThenReturnNotFoundStatus() throws Exception {
        String validBarcode = "12345678";
        ScanningInfo scanningInfo = new ScanningInfo(validBarcode, 1);
        String data = mapper.writeValueAsString(scanningInfo);

        UUID uniquePurchaseId = UUID.randomUUID();
        BigDecimal totalPrice = new BigDecimal("12.50");

        when(this.checkoutService.addItemsToPurchase(org.mockito.Matchers.any(UUID.class), org.mockito.Matchers.any(ScanningInfo.class)))
                .thenThrow(PurchaseNotFoundException.class);

        this.mockMvc.perform(post("/api/purchase/"+uniquePurchaseId.toString()).content(data).contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isNotFound());

    }
}