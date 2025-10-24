package com.srsoft.modapimanager.repository;

import com.srsoft.modapimanager.entity.WooCommerceOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<WooCommerceOrder, Long> {
    
    /**
     * Trova ordini per stato
     */
    List<WooCommerceOrder> findByStatus(String status);
    
    /**
     * Trova ordini per customer ID
     */
    List<WooCommerceOrder> findByCustomerId(Long customerId);
    
    /**
     * Trova ordine per order key
     */
    Optional<WooCommerceOrder> findByOrderKey(String orderKey);
    
    /**
     * Trova ordini per email
     */
    List<WooCommerceOrder> findByEmail(String email);
    
    /**
     * Verifica se esiste un ordine con questo ID
     */
    boolean existsById(Long id);
}