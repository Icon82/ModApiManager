package com.srsoft.modapimanager.service;

import com.srsoft.modapimanager.dto.zonwizard.ZonWizardSaleDTO;
import com.srsoft.modapimanager.entity.WooCommerceOrder;
import com.srsoft.modapimanager.entity.ZonWizardExport;
import com.srsoft.modapimanager.mapper.ZonWizardMapper;
import com.srsoft.modapimanager.repository.OrderRepository;
import com.srsoft.modapimanager.repository.ZonWizardExportRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Service per gestire l'esportazione degli ordini verso ZonWizard
 * CON TRACKING COMPLETO degli export
 */
@Service
public class ZonWizardServiceWithTracking {

    private static final Logger logger = LoggerFactory.getLogger(ZonWizardServiceWithTracking.class);

    @Autowired
    @Qualifier("zonwizardRestTemplate")
    private RestTemplate restTemplate;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ZonWizardExportRepository exportRepository;

    @Autowired
    private ZonWizardMapper zonWizardMapper;

    @Autowired
    private ObjectMapper objectMapper;

    @Value("${zonwizard.api.endpoint.sales:/api/v0/sales}")
    private String salesEndpoint;

    @Value("${zonwizard.export.max-retries:3}")
    private int maxRetries;

    /**
     * Esporta un singolo ordine verso ZonWizard CON TRACKING
     */
    @Transactional
    public Map<String, Object> exportOrderById(Long orderId, boolean isManualExport) throws Exception {
        logger.info("Esportazione ordine {} verso ZonWizard (manual: {})", orderId, isManualExport);

        // Recupera ordine dal database
        Optional<WooCommerceOrder> orderOpt = orderRepository.findById(orderId);
        if (!orderOpt.isPresent()) {
            throw new Exception("Ordine " + orderId + " non trovato nel database locale");
        }

        WooCommerceOrder order = orderOpt.get();

        // Verifica se già esportato con successo (se non è manuale)
        if (!isManualExport && isAlreadyExported(orderId)) {
            logger.info("Ordine {} già esportato con successo, skip", orderId);
            Map<String, Object> result = new HashMap<>();
            result.put("status", "skipped");
            result.put("message", "Ordine già esportato con successo");
            result.put("order_id", orderId);
            return result;
        }

        // Recupera ultimo export (se esiste)
        Optional<ZonWizardExport> lastExportOpt = exportRepository
                .findFirstByOrderIdOrderByLastExportAttemptDesc(orderId);

        ZonWizardExport exportRecord;
        if (lastExportOpt.isPresent() && 
            !ZonWizardExport.ExportStatus.SUCCESS.equals(lastExportOpt.get().getExportStatus())) {
            // Riprova export fallito
            exportRecord = lastExportOpt.get();
            exportRecord.setRetryCount(exportRecord.getRetryCount() + 1);
            exportRecord.setLastExportAttempt(LocalDateTime.now());
        } else {
            // Nuovo export
            exportRecord = ZonWizardExport.builder()
                    .orderId(orderId)
                    .orderNumber(order.getNumber())
                    .exportStatus(ZonWizardExport.ExportStatus.PENDING)
                    .firstExportAttempt(LocalDateTime.now())
                    .lastExportAttempt(LocalDateTime.now())
                    .retryCount(0)
                    .isManualExport(isManualExport)
                    .build();
        }

        // Converti in formato ZonWizard
        ZonWizardSaleDTO saleDTO = zonWizardMapper.toZonWizardSale(order);

        try {
            // Invia a ZonWizard
            Map<String, Object> response = sendSaleToZonWizard(saleDTO);

            // Export riuscito - aggiorna record
            exportRecord.setExportStatus(ZonWizardExport.ExportStatus.SUCCESS);
            exportRecord.setExportSuccessDate(LocalDateTime.now());
            exportRecord.setHttpStatusCode((Integer) response.get("http_status"));
            
            // Salva response ZonWizard
            if (response.get("zonwizard_response") != null) {
                exportRecord.setZonwizardResponse(
                    objectMapper.writeValueAsString(response.get("zonwizard_response"))
                );
                
                // Estrai ID ZonWizard se disponibile
                @SuppressWarnings("unchecked")
                Map<String, Object> zwResponse = (Map<String, Object>) response.get("zonwizard_response");
                if (zwResponse.containsKey("id")) {
                    exportRecord.setZonwizardId(zwResponse.get("id").toString());
                }
            }

            exportRepository.save(exportRecord);
            logger.info("Ordine {} esportato con successo e tracciato", orderId);

            response.put("tracking_id", exportRecord.getId());
            return response;

        } catch (Exception e) {
            // Export fallito - registra errore
            exportRecord.setExportStatus(ZonWizardExport.ExportStatus.FAILED);
            exportRecord.setErrorMessage(e.getMessage());
            
            exportRepository.save(exportRecord);
            logger.error("Errore export ordine {} (tentativo {}): {}", 
                    orderId, exportRecord.getRetryCount(), e.getMessage());

            throw e;
        }
    }

    /**
     * Esporta tutti gli ordini NON ancora esportati
     */
    @Transactional
    public Map<String, Object> exportPendingOrders() {
        logger.info("Esportazione ordini non ancora esportati verso ZonWizard");

        List<Long> pendingOrderIds = exportRepository.findOrderIdsNotExported();
        logger.info("Trovati {} ordini da esportare", pendingOrderIds.size());

        return exportOrdersByIds(pendingOrderIds, false);
    }

    /**
     * Esporta ordini per stato (solo se non già esportati)
     */
    @Transactional
    public Map<String, Object> exportOrdersByStatus(String status, boolean forceReexport) {
        logger.info("Esportazione ordini con stato '{}' (force: {})", status, forceReexport);

        List<WooCommerceOrder> orders = orderRepository.findByStatus(status);
        
        // Filtra ordini già esportati (se non force)
        if (!forceReexport) {
            orders = orders.stream()
                    .filter(order -> !isAlreadyExported(order.getId()))
                    .toList();
        }

        return exportOrders(orders, forceReexport);
    }

    /**
     * Ri-prova export falliti
     */
    @Transactional
    public Map<String, Object> retryFailedExports() {
        logger.info("Ri-prova export falliti (max retries: {})", maxRetries);

        List<ZonWizardExport> failedExports = exportRepository.findFailedExportsForRetry(maxRetries);
        logger.info("Trovati {} export falliti da ri-provare", failedExports.size());

        Map<String, Object> result = new HashMap<>();
        int successCount = 0;
        int errorCount = 0;
        StringBuilder errors = new StringBuilder();

        for (ZonWizardExport failedExport : failedExports) {
            try {
                exportOrderById(failedExport.getOrderId(), false);
                successCount++;
            } catch (Exception e) {
                errorCount++;
                errors.append(String.format("Ordine %d: %s; ", 
                    failedExport.getOrderId(), e.getMessage()));
            }
        }

        result.put("total_retries", failedExports.size());
        result.put("success", successCount);
        result.put("errors", errorCount);
        if (errors.length() > 0) {
            result.put("error_details", errors.toString());
        }

        return result;
    }

    /**
     * Ottiene storico export per un ordine
     */
    public List<ZonWizardExport> getExportHistory(Long orderId) {
        return exportRepository.findByOrderIdOrderByLastExportAttemptDesc(orderId);
    }

    /**
     * Verifica se un ordine è già stato esportato con successo
     */
    public boolean isAlreadyExported(Long orderId) {
        return exportRepository.existsByOrderIdAndExportStatus(
            orderId, 
            ZonWizardExport.ExportStatus.SUCCESS
        );
    }

    /**
     * Ottiene statistiche export
     */
    public Map<String, Object> getExportStatistics() {
        Map<String, Object> stats = new HashMap<>();

        long totalOrders = orderRepository.count();
        long exportedOrders = exportRepository.countByExportStatus(ZonWizardExport.ExportStatus.SUCCESS);
        long failedExports = exportRepository.countByExportStatus(ZonWizardExport.ExportStatus.FAILED);
        long pendingExports = exportRepository.countByExportStatus(ZonWizardExport.ExportStatus.PENDING);

        stats.put("total_orders", totalOrders);
        stats.put("exported_orders", exportedOrders);
        stats.put("failed_exports", failedExports);
        stats.put("pending_exports", pendingExports);
        stats.put("not_exported_orders", totalOrders - exportedOrders);
        stats.put("export_success_rate", 
            totalOrders > 0 ? (exportedOrders * 100.0 / totalOrders) : 0);

        return stats;
    }

    /**
     * Ottiene export recenti (ultimi N giorni)
     */
    public List<ZonWizardExport> getRecentExports(int days) {
        LocalDateTime fromDate = LocalDateTime.now().minusDays(days);
        return exportRepository.findRecentExports(fromDate);
    }

    /**
     * Esporta lista di ordini
     */
    private Map<String, Object> exportOrders(List<WooCommerceOrder> orders, boolean isManualExport) {
        Map<String, Object> result = new HashMap<>();
        int successCount = 0;
        int skipCount = 0;
        int errorCount = 0;
        StringBuilder errors = new StringBuilder();

        for (WooCommerceOrder order : orders) {
            try {
                Map<String, Object> exportResult = exportOrderById(order.getId(), isManualExport);
                
                if ("skipped".equals(exportResult.get("status"))) {
                    skipCount++;
                } else {
                    successCount++;
                }
                
                logger.info("Ordine {} esportato", order.getId());
            } catch (Exception e) {
                errorCount++;
                String errorMsg = String.format("Ordine %d: %s", order.getId(), e.getMessage());
                errors.append(errorMsg).append("; ");
                logger.error("Errore esportazione ordine {}: {}", order.getId(), e.getMessage());
            }
        }

        result.put("total", orders.size());
        result.put("success", successCount);
        result.put("skipped", skipCount);
        result.put("errors", errorCount);
        if (errors.length() > 0) {
            result.put("error_details", errors.toString());
        }

        return result;
    }

    /**
     * Esporta ordini per lista di ID
     */
    private Map<String, Object> exportOrdersByIds(List<Long> orderIds, boolean isManualExport) {
        Map<String, Object> result = new HashMap<>();
        int successCount = 0;
        int errorCount = 0;
        StringBuilder errors = new StringBuilder();

        for (Long orderId : orderIds) {
            try {
                exportOrderById(orderId, isManualExport);
                successCount++;
            } catch (Exception e) {
                errorCount++;
                errors.append(String.format("Ordine %d: %s; ", orderId, e.getMessage()));
            }
        }

        result.put("total", orderIds.size());
        result.put("success", successCount);
        result.put("errors", errorCount);
        if (errors.length() > 0) {
            result.put("error_details", errors.toString());
        }

        return result;
    }

    /**
     * Invia vendita a ZonWizard (metodo privato)
     */
    private Map<String, Object> sendSaleToZonWizard(ZonWizardSaleDTO saleDTO) throws Exception {
        try {
            logger.debug("Invio vendita a ZonWizard: {}", saleDTO.getOrderId());

            HttpEntity<ZonWizardSaleDTO> request = new HttpEntity<>(saleDTO);
            
            ResponseEntity<Map> response = restTemplate.exchange(
                    salesEndpoint,
                    HttpMethod.POST,
                    request,
                    Map.class
            );

            logger.info("Vendita {} inviata con successo a ZonWizard. Status: {}", 
                    saleDTO.getOrderId(), response.getStatusCode());

            Map<String, Object> result = new HashMap<>();
            result.put("status", "success");
            result.put("order_id", saleDTO.getOrderId());
            result.put("zonwizard_response", response.getBody());
            result.put("http_status", response.getStatusCode().value());

            return result;

        } catch (HttpClientErrorException | HttpServerErrorException e) {
            logger.error("Errore ZonWizard per ordine {}: {} - {}", 
                    saleDTO.getOrderId(), e.getStatusCode(), e.getResponseBodyAsString());
            throw new Exception("Errore ZonWizard: " + e.getStatusCode() + " - " + 
                    e.getResponseBodyAsString());
        } catch (Exception e) {
            logger.error("Errore generico export ordine {} a ZonWizard: {}", 
                    saleDTO.getOrderId(), e.getMessage());
            throw new Exception("Errore export: " + e.getMessage());
        }
    }

    /**
     * Test connessione
     */
    public boolean testConnection() {
        try {
            logger.info("Test connessione API ZonWizard");
            ResponseEntity<String> response = restTemplate.getForEntity("/health", String.class);
            logger.info("Test connessione riuscito: {}", response.getStatusCode());
            return true;
        } catch (Exception e) {
            logger.error("Test connessione fallito: {}", e.getMessage());
            return false;
        }
    }
}