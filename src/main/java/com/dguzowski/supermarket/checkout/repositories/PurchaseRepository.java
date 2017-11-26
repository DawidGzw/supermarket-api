package com.dguzowski.supermarket.checkout.repositories;

import com.dguzowski.supermarket.checkout.domain.Purchase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;


/**
 * Spring Data JPA repository for the Purchease entity.
 */
@SuppressWarnings("unused")
@Repository
public interface PurchaseRepository extends JpaRepository<Purchase, UUID> {

}
