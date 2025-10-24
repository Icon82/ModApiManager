package com.srsoft.modapimanager.repository;

import com.srsoft.modapimanager.entity.WooCommerceOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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
    
  // ========== NUOVI METODI CON JOIN FETCH (per ZonWizard Export) ==========
    
    /**
     * ✅ Carica ordine singolo con TUTTE le collezioni necessarie per export ZonWizard
     * Usa questo metodo quando devi esportare un ordine a ZonWizard
     */
    @Query("""
        SELECT DISTINCT o FROM WooCommerceOrder o
        LEFT JOIN FETCH o.lineItems
        LEFT JOIN FETCH o.metaData
        LEFT JOIN FETCH o.shippingLines
        WHERE o.id = :orderId
        """)
    Optional<WooCommerceOrder> findByIdWithAllData(@Param("orderId") Long orderId);
    
    /**
     * ✅ Carica ordini per status con tutte le collezioni
     * Usa questo per export massivo per status
     */
    @Query("""
        SELECT DISTINCT o FROM WooCommerceOrder o
        LEFT JOIN FETCH o.lineItems
        LEFT JOIN FETCH o.metaData
        LEFT JOIN FETCH o.shippingLines
        WHERE o.status = :status
        """)
    List<WooCommerceOrder> findByStatusWithAllData(@Param("status") String status);
    
    /**
     * ✅ Carica ordini per customer con tutte le collezioni
     */
    @Query("""
        SELECT DISTINCT o FROM WooCommerceOrder o
        LEFT JOIN FETCH o.lineItems
        LEFT JOIN FETCH o.metaData
        LEFT JOIN FETCH o.shippingLines
        WHERE o.customerId = :customerId
        """)
    List<WooCommerceOrder> findByCustomerIdWithAllData(@Param("customerId") Long customerId);
    
    /**
     * ✅ Carica tutti gli ordini con collezioni
     * ⚠️ ATTENZIONE: Usa solo per piccoli dataset o con paginazione!
     */
    @Query("""
        SELECT DISTINCT o FROM WooCommerceOrder o
        LEFT JOIN FETCH o.lineItems
        LEFT JOIN FETCH o.metaData
        LEFT JOIN FETCH o.shippingLines
        """)
    List<WooCommerceOrder> findAllWithAllData();
    
    /**
     * ✅ Carica ordini per lista di IDs con tutte le collezioni
     * Utile per export batch di ordini specifici
     */
    @Query("""
        SELECT DISTINCT o FROM WooCommerceOrder o
        LEFT JOIN FETCH o.lineItems
        LEFT JOIN FETCH o.metaData
        LEFT JOIN FETCH o.shippingLines
        WHERE o.id IN :orderIds
        """)
    List<WooCommerceOrder> findByIdInWithAllData(@Param("orderIds") List<Long> orderIds);

}