package com.dguzowski.supermarket.checkout.repositories;

import com.dguzowski.supermarket.checkout.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;


/**
 * Spring Data JPA repository for the Product entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    Optional<Product> findProductByBarcode(String barcode);
}
