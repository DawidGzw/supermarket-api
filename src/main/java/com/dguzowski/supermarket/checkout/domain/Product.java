package com.dguzowski.supermarket.checkout.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.Pattern;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * A Product.
 */
@Entity
@Table(name = "product")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Product implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @Pattern(regexp = "^[0-9]{8}$",message = "Bad barcode pattern {0}")
    @Column(name = "barcode", nullable = false)
    private String barcode;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "price", precision=10, scale=2, nullable = false)
    private BigDecimal price;

    @JsonIgnore
    @Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
    @ElementCollection
    @CollectionTable(uniqueConstraints = { @UniqueConstraint(columnNames = {"product_id", "amount"})})
    @OrderBy("amount ASC")
    private Set<Promotion> promotions = new HashSet<>();

    protected Product(){}

    public Product(String barcode, String name, BigDecimal price) {
        this(barcode, name, price, null);
    }

    public Product(String barcode, String name,  BigDecimal price, String description) {
        this.barcode = barcode;
        this.name = name;
        this.description = description;
        this.price = price;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getBarcode() {
        return barcode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Set<Promotion> getPromotions() {
        return promotions;
    }

    public Set<Promotion> getApplicablePromotions(int amount){
        return this.promotions.stream()
                .filter( promo -> promo.getAmount()<=amount)
                .collect(Collectors.toSet());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Product)) return false;

        Product product = (Product) o;

        if (!id.equals(product.id)) return false;
        return barcode.equals(product.barcode);
    }

    @Override
    public int hashCode() {
        int result = Optional.ofNullable(id)
                .map( id -> id.hashCode())
                .orElse( 0 );
        result = 31 * result + barcode.hashCode();
        return result;
    }
}
