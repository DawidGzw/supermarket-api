package com.dguzowski.supermarket.checkout.domain;

import com.dguzowski.supermarket.checkout.strategy.TotalPriceCalculationStrategy;
import com.dguzowski.supermarket.checkout.strategy.TotalPriceCalculationStrategyProvider;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.UUID;

/**
 * A PurcheaseItem.
 */
@Entity
@Table(name = "purchase_item")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@org.hibernate.annotations.Immutable
public class PurchaseItem implements Serializable {

    private static final long serialVersionUID = 1L;

    @Embeddable
    public static class Id implements Serializable{

        @NotNull
        @Column(name="PURCHASE_UUID")
        private UUID purchaseId;

        @NotNull
        @Column(name="PRODUCT_ID")
        private Long productId;

        protected Id(){}

        public Id(UUID purchaseId, Long productId){
            this.purchaseId=purchaseId;
            this.productId=productId;
        }

    }

    @EmbeddedId
    private Id id;

    @Min(1)
    @NotNull
    @Column(name = "amount", nullable = false, updatable = false)
    private Integer amount;

    @NotNull
    @Column(name = "price", precision=10, scale=2, nullable = false, updatable = false)
    private BigDecimal totalPrice;

    @ManyToOne
    @JoinColumn(name = "PURCHASE_UUID",
    insertable = false,
    updatable = false)
    private Purchase purchase;

    @ManyToOne
    @JoinColumn(name = "PRODUCT_ID",
    insertable = false,
    updatable = false)
    private Product product;

    protected PurchaseItem() {
    }

    public PurchaseItem(Product product, Purchase purchase, int amount){
        this.product = product;
        this.purchase = purchase;
        this.amount = amount;
        this.id = new Id(purchase.getId(), product.getId());
        this.calculateTotalPrice();
        this.purchase.addItem(this);
    }

    public Id getId() {
        return id;
    }

    public Integer getAmount() {
        return amount;
    }

    public void changeAmount(Integer amount) {
        this.amount += amount;
        BigDecimal oldPrice = this.totalPrice;
        this.calculateTotalPrice();
        BigDecimal newPrice = this.totalPrice;
        BigDecimal change = newPrice.subtract(oldPrice);
        this.purchase.changeTotalPrice(change);
        if(this.amount <=0){
            this.amount = 0;
            this.purchase.removeItem(this);
        }
    }

    public Purchase getPurchase() {
        return purchase;
    }

    public Product getProduct() {
        return product;
    }

    protected void calculateTotalPrice(){
        this.totalPrice = TotalPriceCalculationStrategyProvider
                .getCalculationStrategy()
                .calculateTotalPrice(this.product, this.amount);
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PurchaseItem)) return false;

        PurchaseItem that = (PurchaseItem) o;

        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
