package com.dguzowski.supermarket.checkout.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Parent;

import javax.persistence.*;
import javax.validation.constraints.Min;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * A Promotion.
 */
@Embeddable
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Promotion implements Serializable {

    private static final long serialVersionUID = 1L;

    @Min(2)
    @Column(name = "amount", nullable = false)
    private Integer amount;

    @Column(name = "price", precision=10, scale=2)
    private BigDecimal price;

    @Parent
    private Product product;

    protected Promotion(){}

    public Promotion(Product product, int amount, BigDecimal price){
        this.product = product;
        this.amount = amount;
        this.price = price;
        this.product.getPromotions().add(this);
    }

    public Integer getAmount() {
        return amount;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Promotion)) return false;

        Promotion promotion = (Promotion) o;

        if (!amount.equals(promotion.amount)) return false;
        return product.equals(promotion.product);
    }

    @Override
    public int hashCode() {
        int result = amount.hashCode();
        result = 31 * result + product.hashCode();
        return result;
    }
}

