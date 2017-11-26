package com.dguzowski.supermarket.checkout.domain;

import com.dguzowski.supermarket.checkout.strategy.TotalPriceCalculationStrategyProvider;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.Assert.assertThat;

@RunWith(SpringRunner.class)
@DataJpaTest
public class PurchaseIntTest {

    TotalPriceCalculationStrategyProvider provider = new TotalPriceCalculationStrategyProvider("highest_to_lowest_amount");

    @Autowired
    TestEntityManager em;

    private Product createProduct(){
        Product butter = new Product("12345678", "butter", new BigDecimal("2.15"));
        em.persist(butter);
        return butter;
    }

    @Test
    public void whenSavingPurchaseWithPurchaseItemsThenTheyShouldBeFetchable(){
         Product product = this.createProduct();
         UUID randomId = UUID.randomUUID();
         Purchase purchase = new Purchase(randomId);
         new PurchaseItem(product, purchase, 5);
         em.persist(purchase);
         em.flush();

         Purchase fetchedPurchase = em.find(Purchase.class, purchase.getId());
         assertThat(fetchedPurchase, Matchers.notNullValue());
         assertThat(fetchedPurchase.getPurchaseDate(), Matchers.notNullValue());
         assertThat(fetchedPurchase.getItems(), Matchers.hasSize(1));
         assertThat(fetchedPurchase.getId(), Matchers.equalTo(randomId));
         assertThat(fetchedPurchase.getTotalPrice(), Matchers.equalTo(new BigDecimal("2.15").multiply(BigDecimal.valueOf(5))));
    }
}
