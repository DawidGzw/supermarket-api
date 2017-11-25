package com.dguzowski.supermarket.checkout.rest;

import com.dguzowski.supermarket.checkout.exception.ItemDataNotFoundInPurchaseException;
import com.dguzowski.supermarket.checkout.exception.PurchaseDataNotFoundException;
import com.dguzowski.supermarket.checkout.rest.model.PurchaseData;
import com.dguzowski.supermarket.checkout.rest.model.ScanningInfo;
import com.dguzowski.supermarket.checkout.rest.model.TotalPrice;
import com.dguzowski.supermarket.checkout.service.CheckoutService;
import com.dguzowski.supermarket.checkout.util.HeaderUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.hamcrest.Matchers.isEmptyOrNullString;
import static org.hamcrest.Matchers.not;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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
                .andExpect(jsonPath("$.total_price", org.hamcrest.Matchers.is(totalPrice.doubleValue())))
                .andExpect(jsonPath("$.purchase_id", org.hamcrest.Matchers.is(uniquePuechaseId.toString())));
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
                .andExpect(content().bytes(new byte[0]));
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
                .andExpect(content().bytes(new byte[0]));
    }

    @Test
    public void whenProductIsAddedToPurchaseAndScanDataIsValidThenReturnTotalPriceAndOk() throws Exception {
        String validBarcode = "12345678";
        ScanningInfo scanningInfo = new ScanningInfo(validBarcode, 1);
        String data = mapper.writeValueAsString(scanningInfo);

        UUID uniquePurchaseId = UUID.randomUUID();
        BigDecimal totalPrice = new BigDecimal("12.50");

        when(this.checkoutService.addItemsToPurchase(eq(uniquePurchaseId), org.mockito.Matchers.any(ScanningInfo.class))).thenReturn(new TotalPrice(totalPrice));

        this.mockMvc.perform(post("/api/purchase/" + uniquePurchaseId.toString()).content(data).contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk())
                //.andExpect(content().json(this.mapper.writeValueAsString(new PurchaseData(totalPrice, uniquePuechaseId))));
                // It defeated me. jsonPath method tries to check equality of Double and BigDecimal
                .andExpect(jsonPath("$.total_price", org.hamcrest.Matchers.is(totalPrice.doubleValue())));
    }

    @Test
    public void whenProductIsAddedToPurchaseAndScanDataIsInvalidThenReturnNotFoundStatus() throws Exception {
        String validBarcode = "12345678";
        ScanningInfo scanningInfo = new ScanningInfo(validBarcode, 1);
        String data = mapper.writeValueAsString(scanningInfo);

        UUID uniquePurchaseId = UUID.randomUUID();
        BigDecimal totalPrice = new BigDecimal("12.50");


        when(this.checkoutService.addItemsToPurchase(any(UUID.class), any(ScanningInfo.class)))
                .thenThrow(new PurchaseDataNotFoundException(uniquePurchaseId));

        this.mockMvc.perform(post("/api/purchase/" + uniquePurchaseId.toString()).content(data).contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isNotFound());

    }

    @Test
    public void whenExistingPurchaseIsDeletedThenReturnOkStatus() throws Exception {
        UUID uniquePurchaseId = UUID.randomUUID();
        when(this.checkoutService.deletePurchaseItem(eq(uniquePurchaseId), any(Optional.class)))
                .thenReturn(Optional.empty());

        this.mockMvc.perform(delete("/api/purchase/" + uniquePurchaseId.toString()))
                .andExpect(status().isOk());

        verify(this.checkoutService).deletePurchaseItem(eq(uniquePurchaseId), any(Optional.class));
    }

    @Test
    public void whenUnexistingPurchaseIsDeletedThenReturnNotFoundStatus() throws Exception {
        UUID uniquePurchaseId = UUID.randomUUID();
        doThrow(new PurchaseDataNotFoundException(uniquePurchaseId)).
                when(this.checkoutService).deletePurchaseItem(eq(uniquePurchaseId), any(Optional.class));

        this.mockMvc.perform(delete("/api/purchase/" + uniquePurchaseId.toString()))
                .andExpect(status().isNotFound())
                .andExpect(header().string(HeaderUtil.getParamHeaderName(), uniquePurchaseId.toString()))
                .andExpect(header().string(HeaderUtil.getAlertHeaderName(), org.hamcrest.Matchers.not(isEmptyOrNullString())));

        verify(this.checkoutService).deletePurchaseItem(eq(uniquePurchaseId), any(Optional.class));
    }

    @Test
    public void whenProductIsRemovedFromPurchaseAndIsPresentThenReturnTotalPriceAndOkStatus() throws Exception {
        String validBarcode = "12345678";
        ScanningInfo scanningInfo = new ScanningInfo(validBarcode, 1);
        String data = mapper.writeValueAsString(scanningInfo);

        UUID uniquePurchaseId = UUID.randomUUID();
        BigDecimal totalCost = new BigDecimal("15.50");
        TotalPrice totalPrice = new TotalPrice(totalCost);

        when(this.checkoutService.deletePurchaseItem(eq(uniquePurchaseId), any(Optional.class)))
                .thenReturn(Optional.of(totalPrice));

        this.mockMvc.perform(delete("/api/purchase/" + uniquePurchaseId.toString()).content(data).contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total_price", org.hamcrest.Matchers.is(totalCost.doubleValue())));
    }

    @Test
    public void whenAbsentProductIsRemovedFromPurchaseThenReturnNotFoundStatus() throws Exception {
        String validBarcode = "12345678";
        ScanningInfo scanningInfo = new ScanningInfo(validBarcode, 1);
        String data = mapper.writeValueAsString(scanningInfo);

        UUID uniquePurchaseId = UUID.randomUUID();
        BigDecimal totalCost = new BigDecimal("15.50");
        TotalPrice totalPrice = new TotalPrice(totalCost);

        when(this.checkoutService.deletePurchaseItem(eq(uniquePurchaseId), any(Optional.class)))
                .thenThrow(new ItemDataNotFoundInPurchaseException(uniquePurchaseId, validBarcode));

        this.mockMvc.perform(delete("/api/purchase/" + uniquePurchaseId.toString()).content(data).contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isNotFound())
                .andExpect(header().string(HeaderUtil.getParamHeaderName(),
                        StringUtils.arrayToCommaDelimitedString(new Object[]{uniquePurchaseId, validBarcode})))
                .andExpect(header().string(HeaderUtil.getAlertHeaderName(), not(isEmptyOrNullString())));
    }

    @Test
    public void whenFinalizingExistingPurchaseThenReturnOkStatus() throws Exception {
        UUID uniquePurchaseId = UUID.randomUUID();
        doNothing().when(this.checkoutService).savePurchase(uniquePurchaseId);

        this.mockMvc.perform(get("/api/purchase/finish/"+uniquePurchaseId.toString()))
                .andExpect(status().isOk());
        verify(this.checkoutService).savePurchase(uniquePurchaseId);
    }

    @Test
    public void whenFinalizingNotExistingPurchaseThenReturnSomeStatus() throws Exception {
        UUID uniquePurchaseId = UUID.randomUUID();
        doThrow(new PurchaseDataNotFoundException(uniquePurchaseId)).when(this.checkoutService).savePurchase(uniquePurchaseId);

        this.mockMvc.perform(get("/api/purchase/finish/"+uniquePurchaseId.toString()))
                .andExpect(status().isNotFound())
                .andExpect(header().string(HeaderUtil.getParamHeaderName(),uniquePurchaseId.toString()))
                .andExpect(header().string(HeaderUtil.getAlertHeaderName(),not(isEmptyOrNullString())));
    }
}