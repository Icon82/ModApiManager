package com.srsoft.modapimanager.dto.zonwizard;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO per rappresentare le statistiche di export verso ZonWizard
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExportStatisticsDTO {

    /**
     * Totale ordini nel database
     */
    @JsonProperty("total_orders")
    private Long totalOrders;

    /**
     * Ordini esportati con successo
     */
    @JsonProperty("exported_orders")
    private Long exportedOrders;

    /**
     * Export falliti
     */
    @JsonProperty("failed_exports")
    private Long failedExports;

    /**
     * Export in attesa
     */
    @JsonProperty("pending_exports")
    private Long pendingExports;

    /**
     * Ordini non ancora esportati
     */
    @JsonProperty("not_exported_orders")
    private Long notExportedOrders;

    /**
     * Percentuale successo export
     */
    @JsonProperty("export_success_rate")
    private Double exportSuccessRate;

    /**
     * Data ultima statistica
     */
    @JsonProperty("updated_at")
    private LocalDateTime updatedAt;

    /**
     * Export oggi
     */
    @JsonProperty("exports_today")
    private Long exportsToday;

    /**
     * Export questa settimana
     */
    @JsonProperty("exports_this_week")
    private Long exportsThisWeek;

    /**
     * Export falliti recuperabili (con retry disponibili)
     */
    @JsonProperty("recoverable_failed_exports")
    private Long recoverableFailedExports;
}