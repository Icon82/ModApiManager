package com.srsoft.modapimanager.controller;


import com.srsoft.modapimanager.dto.OrderDTO;
import com.srsoft.modapimanager.service.WooCommerceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/woocommerce")
public class WooCommerceController {

    @Autowired
    private WooCommerceService wooCommerceService;

    // ============================================================
    // ENDPOINTS DI SINCRONIZZAZIONE (WooCommerce → Database)
    // ============================================================

    /**
     * POST /api/woocommerce/sync/orders
     * Sincronizza TUTTI gli ordini da WooCommerce al database
     */
    @PostMapping("/sync/orders")
    public ResponseEntity<List<OrderDTO>> syncAllOrders() {
        List<OrderDTO> orders = wooCommerceService.syncAllOrders();
        return ResponseEntity.ok(orders);
    }

    /**
     * POST /api/woocommerce/sync/orders/{id}
     * Sincronizza un SINGOLO ordine da WooCommerce al database
     */
    @PostMapping("/sync/orders/{id}")
    public ResponseEntity<OrderDTO> syncOrderById(@PathVariable Long id) {
        OrderDTO order = wooCommerceService.syncOrderById(id);
        if (order != null) {
            return ResponseEntity.ok(order);
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * POST /api/woocommerce/sync/orders/status/{status}
     * Sincronizza ordini per STATO da WooCommerce al database
     */
    @PostMapping("/sync/orders/status/{status}")
    public ResponseEntity<List<OrderDTO>> syncOrdersByStatus(
            @PathVariable String status,
            @RequestParam(defaultValue = "100") int perPage,
            @RequestParam(defaultValue = "1") int page) {
        
        List<OrderDTO> orders = wooCommerceService.syncOrdersByStatus(status, perPage, page);
        return ResponseEntity.ok(orders);
    }

    /**
     * POST /api/woocommerce/sync/orders/recent
     * Sincronizza ordini RECENTI da WooCommerce al database
     */
    @PostMapping("/sync/orders/recent")
    public ResponseEntity<List<OrderDTO>> syncRecentOrders(
            @RequestParam(defaultValue = "50") int limit) {
        
        List<OrderDTO> orders = wooCommerceService.syncRecentOrders(limit);
        return ResponseEntity.ok(orders);
    }

    // ============================================================
    // ENDPOINTS DI LETTURA (Database locale)
    // Restituiscono DTO, non entity
    // ============================================================

    /**
     * GET /api/woocommerce/orders
     * Recupera tutti gli ordini dal DATABASE LOCALE
     */
    @GetMapping("/orders")
    public ResponseEntity<List<OrderDTO>> getAllOrdersFromDB() {
        List<OrderDTO> orders = wooCommerceService.getAllOrdersFromDB();
        return ResponseEntity.ok(orders);
    }

    /**
     * GET /api/woocommerce/orders/{id}
     * Recupera un ordine dal DATABASE LOCALE
     */
    @GetMapping("/orders/{id}")
    public ResponseEntity<OrderDTO> getOrderFromDB(@PathVariable Long id) {
        OrderDTO order = wooCommerceService.getOrderFromDB(id);
        if (order != null) {
            return ResponseEntity.ok(order);
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * GET /api/woocommerce/orders/status/{status}
     * Recupera ordini per stato dal DATABASE LOCALE
     */
    @GetMapping("/orders/status/{status}")
    public ResponseEntity<List<OrderDTO>> getOrdersByStatusFromDB(
            @PathVariable String status) {
        
        List<OrderDTO> orders = wooCommerceService.getOrdersByStatusFromDB(status);
        return ResponseEntity.ok(orders);
    }

    /**
     * GET /api/woocommerce/orders/{id}/exists
     * Verifica se un ordine esiste nel database
     */
    @GetMapping("/orders/{id}/exists")
    public ResponseEntity<Boolean> orderExists(@PathVariable Long id) {
        boolean exists = wooCommerceService.orderExistsInDB(id);
        return ResponseEntity.ok(exists);
    }

    // ============================================================
    // ENDPOINTS DI AGGIORNAMENTO E CANCELLAZIONE
    // ============================================================

    /**
     * PATCH /api/woocommerce/orders/{id}/status
     * Aggiorna lo stato su WooCommerce E nel database
     */
    @PatchMapping("/orders/{id}/status")
    public ResponseEntity<OrderDTO> updateOrderStatus(
            @PathVariable Long id,
            @RequestParam String status) {
        
        OrderDTO updatedOrder = wooCommerceService.updateOrderStatus(id, status);
        if (updatedOrder != null) {
            return ResponseEntity.ok(updatedOrder);
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * DELETE /api/woocommerce/orders/{id}
     * Elimina un ordine dal DATABASE LOCALE (NON da WooCommerce!)
     */
    @DeleteMapping("/orders/{id}")
    public ResponseEntity<Void> deleteOrderFromDB(@PathVariable Long id) {
        if (wooCommerceService.orderExistsInDB(id)) {
            wooCommerceService.deleteOrderFromDB(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}