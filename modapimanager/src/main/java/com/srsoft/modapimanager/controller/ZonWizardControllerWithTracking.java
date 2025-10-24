package com.srsoft.modapimanager.controller;

import com.srsoft.modapimanager.entity.ZonWizardExport;
import com.srsoft.modapimanager.service.ZonWizardServiceWithTracking;

import io.swagger.v3.oas.annotations.tags.Tag;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller REST per gestire l'esportazione degli ordini verso ZonWizard
 * CON TRACKING COMPLETO
 * 
 * Endpoint base: /api/zonwizard
 */
@RestController
@RequestMapping("/zonwizard")
@Tag(name = "40.Manage ZooWizard  ", description = "API per la Gestione della comunicazione con ZooWizard")
public class ZonWizardControllerWithTracking {

    private static final Logger logger = LoggerFactory.getLogger(ZonWizardControllerWithTracking.class);

    @Autowired
    private ZonWizardServiceWithTracking zonWizardService;

    /**
     * Esporta un singolo ordine verso ZonWizard
     * 
     * POST /api/zonwizard/export/{orderId}?manual=true
     * 
     * @param orderId ID dell'ordine nel database locale
     * @param manual Se true, forza re-export anche se già esportato
     * @return Risposta con esito dell'esportazione
     */
    @PostMapping("/export/{orderId}")
    public ResponseEntity<Map<String, Object>> exportOrder(
            @PathVariable Long orderId,
            @RequestParam(defaultValue = "false") boolean manual) {
        try {
            logger.info("Richiesta export ordine {} verso ZonWizard (manual: {})", orderId, manual);
            
            Map<String, Object> result = zonWizardService.exportOrderById(orderId, manual);
            
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            logger.error("Errore export ordine {}: {}", orderId, e.getMessage());
            
            Map<String, Object> error = new HashMap<>();
            error.put("status", "error");
            error.put("message", e.getMessage());
            error.put("order_id", orderId);
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * Esporta solo ordini NON ancora esportati
     * 
     * POST /api/zonwizard/export/pending
     * 
     * @return Riepilogo esportazione
     */
    @PostMapping("/export/pending")
    public ResponseEntity<Map<String, Object>> exportPendingOrders() {
        try {
            logger.info("Richiesta export ordini pending verso ZonWizard");
            
            Map<String, Object> result = zonWizardService.exportPendingOrders();
            
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            logger.error("Errore export ordini pending: {}", e.getMessage());
            
            Map<String, Object> error = new HashMap<>();
            error.put("status", "error");
            error.put("message", e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * Esporta ordini per stato (con controllo duplicati)
     * 
     * POST /api/zonwizard/export/status/{status}?force=false
     * 
     * @param status Stato ordini (processing, completed, etc.)
     * @param force Se true, re-esporta anche ordini già esportati
     * @return Riepilogo esportazione
     */
    @PostMapping("/export/status/{status}")
    public ResponseEntity<Map<String, Object>> exportOrdersByStatus(
            @PathVariable String status,
            @RequestParam(defaultValue = "false") boolean force) {
        try {
            logger.info("Richiesta export ordini status '{}' (force: {})", status, force);
            
            Map<String, Object> result = zonWizardService.exportOrdersByStatus(status, force);
            
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            logger.error("Errore export ordini status {}: {}", status, e.getMessage());
            
            Map<String, Object> error = new HashMap<>();
            error.put("status", "error");
            error.put("message", e.getMessage());
            error.put("order_status", status);
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * Ri-prova export falliti
     * 
     * POST /api/zonwizard/export/retry-failed
     * 
     * @return Riepilogo retry
     */
    @PostMapping("/export/retry-failed")
    public ResponseEntity<Map<String, Object>> retryFailedExports() {
        try {
            logger.info("Richiesta retry export falliti");
            
            Map<String, Object> result = zonWizardService.retryFailedExports();
            
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            logger.error("Errore retry export falliti: {}", e.getMessage());
            
            Map<String, Object> error = new HashMap<>();
            error.put("status", "error");
            error.put("message", e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * Verifica se un ordine è già stato esportato
     * 
     * GET /api/zonwizard/export/check/{orderId}
     * 
     * @param orderId ID ordine
     * @return Status export
     */
    @GetMapping("/export/check/{orderId}")
    public ResponseEntity<Map<String, Object>> checkExportStatus(@PathVariable Long orderId) {
        logger.info("Verifica stato export ordine {}", orderId);
        
        Map<String, Object> result = new HashMap<>();
        boolean isExported = zonWizardService.isAlreadyExported(orderId);
        
        result.put("order_id", orderId);
        result.put("is_exported", isExported);
        result.put("status", isExported ? "EXPORTED" : "NOT_EXPORTED");
        
        return ResponseEntity.ok(result);
    }

    /**
     * Ottiene storico export per un ordine
     * 
     * GET /api/zonwizard/export/history/{orderId}
     * 
     * @param orderId ID ordine
     * @return Lista tentativi export
     */
    @GetMapping("/export/history/{orderId}")
    public ResponseEntity<List<ZonWizardExport>> getExportHistory(@PathVariable Long orderId) {
        logger.info("Richiesta storico export ordine {}", orderId);
        
        List<ZonWizardExport> history = zonWizardService.getExportHistory(orderId);
        
        return ResponseEntity.ok(history);
    }

    /**
     * Ottiene export recenti
     * 
     * GET /api/zonwizard/export/recent?days=7
     * 
     * @param days Numero giorni
     * @return Lista export recenti
     */
    @GetMapping("/export/recent")
    public ResponseEntity<List<ZonWizardExport>> getRecentExports(
            @RequestParam(defaultValue = "7") int days) {
        logger.info("Richiesta export recenti (ultimi {} giorni)", days);
        
        List<ZonWizardExport> recentExports = zonWizardService.getRecentExports(days);
        
        return ResponseEntity.ok(recentExports);
    }

    /**
     * Ottiene statistiche export complete
     * 
     * GET /api/zonwizard/stats
     * 
     * @return Statistiche dettagliate
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getExportStatistics() {
        logger.info("Richiesta statistiche export");
        
        Map<String, Object> stats = zonWizardService.getExportStatistics();
        
        return ResponseEntity.ok(stats);
    }

    /**
     * Testa la connessione con ZonWizard API
     * 
     * GET /api/zonwizard/test-connection
     * 
     * @return Esito del test
     */
    @GetMapping("/test-connection")
    public ResponseEntity<Map<String, Object>> testConnection() {
        logger.info("Test connessione ZonWizard API");
        
        Map<String, Object> result = new HashMap<>();
        boolean isConnected = zonWizardService.testConnection();
        
        result.put("connected", isConnected);
        result.put("message", isConnected ? 
                "Connessione ZonWizard API funzionante" : 
                "Errore connessione ZonWizard API");
        
        return ResponseEntity.ok(result);
    }

    /**
     * Endpoint di health check
     * 
     * GET /api/zonwizard/health
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> healthCheck() {
        Map<String, String> health = new HashMap<>();
        health.put("status", "UP");
        health.put("service", "ZonWizard Integration with Tracking");
        health.put("timestamp", LocalDateTime.now().toString());
        
        return ResponseEntity.ok(health);
    }
}