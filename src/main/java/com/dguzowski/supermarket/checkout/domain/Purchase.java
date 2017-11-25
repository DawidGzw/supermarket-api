package com.dguzowski.supermarket.checkout.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.*;

/**
 * A Purchase.
 */
@Entity
@Table(name = "purchase")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Purchase implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name="id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "total_price", precision=10, scale=2, nullable=false, updatable=false)
    private BigDecimal totalPrice;

    @Temporal(TemporalType.TIMESTAMP)
    @CreationTimestamp
    @Column(name = "purchase_date", updatable = false, nullable = false)
    private Date purchaseDate;

    @OneToMany(mappedBy = "purchase", fetch = FetchType.EAGER)
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Set<PurchaseItem> items = new HashSet<>();

    protected Purchase() {}

    public Purchase(UUID id) {
        this.id = id;
    }

    public UUID getId() {
        return id;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public Date getPurchaseDate() {
        return purchaseDate;
    }

    public Set<PurchaseItem> getItems() {
        return items;
    }

    public void addItem(PurchaseItem purcheaseItem) {
        this.changeTotalPrice(purcheaseItem.getTotalPrice());
        this.items.add(purcheaseItem);
    }

    public void removeItem(PurchaseItem purcheaseItem) {
        this.changeTotalPrice(purcheaseItem.getTotalPrice().negate());
        this.items.remove(purcheaseItem);
    }

    public void changeTotalPrice(BigDecimal change) {
        this.totalPrice.add(change);
    }

    public Optional<PurchaseItem> getPurchaseItemByProduct(final Product product){
        return this.items.stream()
                .filter( item -> item.getProduct().equals(product))
                .findFirst();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Purchase)) return false;

        Purchase purchase = (Purchase) o;

        return id.equals(purchase.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
