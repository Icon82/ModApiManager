package com.srsoft.modapimanager.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Entity per tracciare gli export degli ordini verso ZonWizard
 * 
 * Questa tabella mantiene lo storico di tutte le esportazioni:
 * - Ordini esportati con successo
 * - Ordini con errori di export
 * - Numero tentativi
 * - Timestamp operazioni
 */
@Entity
@Table(name = "zonwizard_exports")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ZonWizardExport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * ID dell'ordine WooCommerce
     */
    @Column(name = "order_id", nullable = false)
    private Long orderId;

    /**
     * Numero ordine WooCommerce (leggibile)
     */
    @Column(name = "order_number")
    private String orderNumber;

    /**
     * Stato dell'export
     * SUCCESS, FAILED, PENDING
     */
    @Column(name = "export_status", nullable = false, length = 20)
    private String exportStatus;

    /**
     * Data e ora del primo tentativo di export
     */
    @Column(name = "first_export_attempt")
    private LocalDateTime firstExportAttempt;

    /**
     * Data e ora dell'ultimo tentativo di export
     */
    @Column(name = "last_export_attempt")
    private LocalDateTime lastExportAttempt;

    /**
     * Data e ora dell'export riuscito (se SUCCESS)
     */
    @Column(name = "export_success_date")
    private LocalDateTime exportSuccessDate;

    /**
     * Numero di tentativi effettuati
     */
    @Column(name = "retry_count")
    private Integer retryCount;

    /**
     * HTTP Status code della risposta ZonWizard
     */
    @Column(name = "http_status_code")
    private Integer httpStatusCode;

    /**
     * ID assegnato da ZonWizard all'ordine (se disponibile)
     */
    @Column(name = "zonwizard_id", length = 255)
    private String zonwizardId;

    /**
     * Messaggio di errore (se FAILED)
     */
    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;

    /**
     * Risposta completa da ZonWizard (JSON)
     */
    @Column(name = "zonwizard_response", columnDefinition = "TEXT")
    private String zonwizardResponse;

    /**
     * Note aggiuntive
     */
    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    /**
     * Flag per indicare se è un re-export manuale
     */
    @Column(name = "is_manual_export")
    private Boolean isManualExport;

    /**
     * Relazione con l'ordine WooCommerce
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", insertable = false, updatable = false)
    private WooCommerceOrder order;

    /**
     * Timestamp creazione record
     */
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    /**
     * Timestamp ultimo aggiornamento
     */
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (retryCount == null) {
            retryCount = 0;
        }
        if (isManualExport == null) {
            isManualExport = false;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    /**
     * Stati possibili dell'export
     */
    public static class ExportStatus {
        public static final String SUCCESS = "SUCCESS";
        public static final String FAILED = "FAILED";
        public static final String PENDING = "PENDING";
    }
}