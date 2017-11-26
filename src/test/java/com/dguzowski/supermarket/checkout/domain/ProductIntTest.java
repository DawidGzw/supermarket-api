package com.dguzowski.supermarket.checkout.domain;

import com.dguzowski.supermarket.checkout.repositories.ProductRepository;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

import javax.validation.ConstraintViolationException;
import java.math.BigDecimal;
import java.util.Iterator;
import java.util.Set;

import static org.junit.Assert.assertThat;

@RunWith(SpringRunner.class)
@DataJpaTest
public class ProductIntTest {

    private static final String VALID_BARCODE = "12345678";

    @Autowired
    TestEntityManager em;

    @Autowired
    ProductRepository repo;

    private Product createValidProduct(){
        Product product = new Product(VALID_BARCODE, "orange juice", new BigDecimal("2.25"));
        return product;
    }

    @Test
    public void whenSavingValidProductThenItShouldBeReachable(){
        Product product = this.createValidProduct();
        em.persist(product);
        em.flush();
        assertThat(product.getId(), Matchers.notNullValue());

        Product p = em.find(Product.class, product.getId());
        assertThat(p, Matchers.notNullValue());
        assertThat(p, Matchers.equalTo(product));
    }

    @Test(expected = ConstraintViolationException.class)
    public void whenSavingProductWithInvalidBarcodeThanThrowException(){
        Product product = new Product("wqwrwrqwr", "apple juice", new BigDecimal("2.25"));
        em.persist(product);
        em.flush();
    }

    @Test
    public void whenSavingProductWithCollectionsOfPromotionsThenThatCollectionShouldBeFetchableAndOrderedByAmount(){
        Product product = this.createValidProduct();
        Promotion smallPromo = new Promotion(product, 2, new BigDecimal("4.10"));
        Promotion biggerPromo = new Promotion(product, 4, new BigDecimal("8.00"));
        em.persist(product);
        em.flush();

        Product fetchedProduct = em.find(Product.class, product.getId());
        Set<Promotion> promotions = fetchedProduct.getPromotions();
        assertThat(promotions, Matchers.hasSize(2));
        Iterator<Promotion> promos = promotions.iterator();
        assertThat(promos.next(), Matchers.equalTo(smallPromo));
        assertThat(promos.next(), Matchers.equalTo(biggerPromo));

    }

    @Test
    public void whenSearchigExistingProductByBarcodeThenItShouldBeReturned(){
        Product product = this.createValidProduct();
        this.repo.save(product);
        this.repo.flush();

        Product fetchedProduct = this.repo.findProductByBarcode(VALID_BARCODE).get();
        assertThat(fetchedProduct, Matchers.notNullValue());
    }

}
