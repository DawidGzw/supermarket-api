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
import com.dguzowski.supermarket.checkout.strategy.TotalPriceCalculationStrategyProvider;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.Assert.*;
import static org.springframework.test.util.ReflectionTestUtils.getField;

@RunWith(SpringRunner.class)
public class CheckoutServiceImplTest {

    @MockBean
    private PurchaseRepository purchaseRepository;

    @MockBean
    private ProductRepository productRepository;

    private CheckoutService checkoutService;

    private TotalPriceCalculationStrategyProvider provider = new TotalPriceCalculationStrategyProvider("highest_to_lowest_amount");

    @Before
    public void setUp() {
        this.checkoutService = new CheckoutServiceImpl(this.productRepository, this.purchaseRepository);
    }

    private Product createNewProduct(String barcode, String name, BigDecimal price) {
        return new Product(barcode, name, price);
    }

    private Purchase createNewPurchase(String barcode, int amount, BigDecimal unitPrice) {
        Product product = this.createNewProduct(barcode, "randomProduct", unitPrice);
        ReflectionTestUtils.setField(product, "id", 1L);
        Purchase purchase = new Purchase(UUID.randomUUID());
        PurchaseItem purchaseItem = new PurchaseItem(product, purchase, amount);
        return purchase;
    }

    @Test
    public void whenNewPurchaseIsCreatedThenReturnPurchaseData() throws Exception {
        String barcode = "12345678";
        int amount = 2;
        Product productToReturn = this.createNewProduct(barcode, "returnedProduct", new BigDecimal("2.50"));
        Mockito.when(productRepository.findProductByBarcode(barcode)).thenReturn(Optional.ofNullable(productToReturn));
        ScanningInfo info = new ScanningInfo(barcode, amount);
        PurchaseData data = this.checkoutService.newPurchease(info);
        assertThat(data.getTotalPrice(), Matchers.equalTo(new BigDecimal("5.00")));
    }

    @Test(expected = ProductDataNotFoundException.class)
    public void whenNewPurchaseIsCreatedAndProductIsNotPresentThenThrowProductNotFoundException() throws Exception {
        String barcode = "12345678";
        int amount = 1;
        Product productToReturn = this.createNewProduct(barcode, "returnedProduct", new BigDecimal("2.50"));
        Mockito.when(productRepository.findProductByBarcode(barcode)).thenReturn(Optional.ofNullable(null));
        ScanningInfo info = new ScanningInfo(barcode, amount);
        PurchaseData data = this.checkoutService.newPurchease(info);
    }

    @Test
    public void whenAddingNewItemToExistingPurchaseThenReturnTotalPrice() throws Exception {
        Purchase existingPurchase = this.addRandomPurchaseToServiceMap();

        String anotherProductBarcode = "12345678";
        int anotherProductAmount = 1;
        BigDecimal anotherProductUnitPrice = new BigDecimal("3.00");
        Product anotherProduct = this.createNewProduct(anotherProductBarcode, "anotherProduct", anotherProductUnitPrice);

        Mockito.when(this.productRepository.findProductByBarcode(anotherProductBarcode))
                .thenReturn(Optional.ofNullable(anotherProduct));
        TotalPrice totalPrice = this.checkoutService.addItemsToPurchase(existingPurchase.getId(), new ScanningInfo(anotherProductBarcode, anotherProductAmount));
        assertThat(totalPrice.getTotalPrice(), Matchers.equalTo(new BigDecimal(2.50).multiply(BigDecimal.valueOf(2)).add(new BigDecimal("3.00"))));
    }

    @Test
    public void whenAddingQuantityToExistingItemInExistingPurchaseThenReturnTotalPrice() throws Exception {
        Purchase existingPurchase = this.addRandomPurchaseToServiceMap();

        int addedAmount = 2;
        String existingProductBarcode = existingPurchase.getItems().iterator().next().getProduct().getBarcode();
        Mockito.when(this.productRepository.findProductByBarcode(existingProductBarcode))
                .thenReturn(Optional.ofNullable(existingPurchase.getItems().iterator().next().getProduct()));
        BigDecimal oldTotalPrice = existingPurchase.getTotalPrice();
        TotalPrice totalPrice = this.checkoutService.addItemsToPurchase(existingPurchase.getId(), new ScanningInfo(
                existingPurchase.getItems().iterator().next().getProduct().getBarcode(),
                addedAmount
        ));
        BigDecimal unitPrice = existingPurchase.getItems().iterator().next().getProduct().getPrice();
        assertThat(totalPrice.getTotalPrice(), Matchers.equalTo(oldTotalPrice.add(unitPrice.multiply(BigDecimal.valueOf(addedAmount)))));
    }

    @Test(expected = PurchaseDataNotFoundException.class)
    public void whenAddingNewItemToUnexistingPurchaseThenThrowException() throws Exception {
        Purchase existingPurchase = this.addRandomPurchaseToServiceMap();

        String anotherProductBarcode = "12345678";
        int anotherProductAmount = 1;
        BigDecimal anotherProductUnitPrice = new BigDecimal("3.00");
        Product anotherProduct = this.createNewProduct(anotherProductBarcode, "anotherProduct", anotherProductUnitPrice);

        Mockito.when(this.productRepository.findProductByBarcode(anotherProductBarcode))
                .thenReturn(Optional.ofNullable(anotherProduct));
        TotalPrice totalPrice = this.checkoutService.addItemsToPurchase(UUID.randomUUID(), new ScanningInfo(anotherProductBarcode, anotherProductAmount));
    }


    @Test(expected = ProductDataNotFoundException.class)
    public void whenAddingNewItemOfUexistingProductToExistingPurchaseThenThrowException() throws Exception {
        Purchase existingPurchase = this.addRandomPurchaseToServiceMap();

        String anotherProductBarcode = "12345678";
        int anotherProductAmount = 1;
        BigDecimal anotherProductUnitPrice = new BigDecimal("3.00");
        Product anotherProduct = this.createNewProduct(anotherProductBarcode, "anotherProduct", anotherProductUnitPrice);

        Mockito.when(this.productRepository.findProductByBarcode(org.mockito.Matchers.any(String.class)))
                .thenReturn(Optional.ofNullable(null));
        TotalPrice totalPrice = this.checkoutService.addItemsToPurchase(existingPurchase.getId(), new ScanningInfo("13542674", anotherProductAmount));
    }

    private Purchase addRandomPurchaseToServiceMap(){
        Map<UUID, Purchase> purchases = (Map<UUID, Purchase>) ReflectionTestUtils.getField(this.checkoutService, "pendingPurchases");
        String productBarcode = "87654321";
        int amount = 2;
        BigDecimal unitPrice = new BigDecimal("2.50");
        Purchase existingPurchase = this.createNewPurchase(productBarcode, amount, unitPrice);
        purchases.put(existingPurchase.getId(), existingPurchase);
        return existingPurchase;
    }

    @Test
    public void deletePurchase() throws Exception {
        Purchase existingPurchase = this.addRandomPurchaseToServiceMap();
        this.checkoutService.deletePurchase(existingPurchase.getId());
        Map<UUID,Purchase> purchaseMap = (Map<UUID, Purchase>) ReflectionTestUtils.getField(this.checkoutService, "pendingPurchases");
        assertThat(purchaseMap.entrySet(), Matchers.empty());
    }

    @Test
    public void whenDeletingOneExistingPurchaseItemsPieceThenReturnTotalPrice() {
        Purchase existingPurchase = this.addRandomPurchaseToServiceMap();
        Product product = existingPurchase.getItems().iterator().next().getProduct();
        String barcode = product.getBarcode();
        Mockito.when(this.productRepository.findProductByBarcode(barcode)).thenReturn(Optional.ofNullable(product));
        Optional<TotalPrice> totalPrice = this.checkoutService.deletePurchaseItem(existingPurchase.getId(), Optional.ofNullable(
                new ScanningInfo(barcode, 1)));
        assertThat(totalPrice.get().getTotalPrice(), Matchers.equalTo(new BigDecimal("2.50")));
    }

    @Test
    public void whenDeletingMoreThanPresentExistingPurchaseItemsPieceThenReturnZeroTotalPrice() {
        Purchase existingPurchase = this.addRandomPurchaseToServiceMap();
        Product product = existingPurchase.getItems().iterator().next().getProduct();
        String barcode = product.getBarcode();
        Mockito.when(this.productRepository.findProductByBarcode(barcode)).thenReturn(Optional.ofNullable(product));
        Optional<TotalPrice> totalPrice = this.checkoutService.deletePurchaseItem(existingPurchase.getId(), Optional.ofNullable(
                new ScanningInfo(barcode, 10)));
        assertThat(totalPrice.get().getTotalPrice(), Matchers.equalTo(new BigDecimal("0.00")));
    }

    @Test(expected = ProductDataNotFoundException.class)
    public void whenDeletingUnexistingProductThrowException() {
        Purchase existingPurchase = this.addRandomPurchaseToServiceMap();
        Product product = existingPurchase.getItems().iterator().next().getProduct();
        String barcode = product.getBarcode();
        Mockito.when(this.productRepository.findProductByBarcode(barcode)).thenReturn(Optional.ofNullable(null));
        Optional<TotalPrice> totalPrice = this.checkoutService.deletePurchaseItem(existingPurchase.getId(), Optional.ofNullable(
                new ScanningInfo(barcode, 10)));
    }

    @Test(expected = PurchaseDataNotFoundException.class)
    public void whenDeletingItemFromUnexistingPurchaseThenThrowException() {
        Purchase existingPurchase = this.addRandomPurchaseToServiceMap();
        Product product = existingPurchase.getItems().iterator().next().getProduct();
        String barcode = product.getBarcode();
        Mockito.when(this.productRepository.findProductByBarcode(barcode)).thenReturn(Optional.ofNullable(product));
        Optional<TotalPrice> totalPrice = this.checkoutService.deletePurchaseItem(UUID.randomUUID(), Optional.ofNullable(
                new ScanningInfo(barcode, 10)));
    }

    @Test(expected = ItemDataNotFoundInPurchaseException.class)
    public void whenDeletingProductItemNotPresentInPurchaseThenThrowException() {
        Purchase existingPurchase = this.addRandomPurchaseToServiceMap();
        String barcode = "11111111";
        Product product = this.createNewProduct(barcode, "prod2", new BigDecimal("13.46"));
        ReflectionTestUtils.setField(product, "id", 2L);
        Mockito.when(this.productRepository.findProductByBarcode(barcode)).thenReturn(Optional.ofNullable(product));
        Optional<TotalPrice> totalPrice = this.checkoutService.deletePurchaseItem(existingPurchase.getId(), Optional.ofNullable(
                new ScanningInfo(barcode, 10)));
    }


    @Test
    public void whenSavingExistingPurchaseThanDoNothing() throws Exception {
        Purchase existingPurchase = this.addRandomPurchaseToServiceMap();
        Mockito.when(this.purchaseRepository.save(existingPurchase)).thenReturn(existingPurchase);
        this.checkoutService.savePurchase(existingPurchase.getId());
        Mockito.verify(this.purchaseRepository).save(existingPurchase);
    }

    @Test(expected = PurchaseDataNotFoundException.class)
    public void whenSavingUnexistingPurchaseThanThrowException() throws Exception {
        Purchase existingPurchase = this.addRandomPurchaseToServiceMap();
        Mockito.when(this.purchaseRepository.save(existingPurchase)).thenReturn(existingPurchase);
        this.checkoutService.savePurchase(UUID.randomUUID());
    }

}