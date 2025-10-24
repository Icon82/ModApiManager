package com.srsoft.modapimanager.repository;

import com.srsoft.modapimanager.entity.ZonWizardExport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository per gestire lo storico export ZonWizard
 */
@Repository
public interface ZonWizardExportRepository extends JpaRepository<ZonWizardExport, Long> {

    /**
     * Trova l'ultimo export per un ordine specifico
     */
    Optional<ZonWizardExport> findFirstByOrderIdOrderByLastExportAttemptDesc(Long orderId);

    /**
     * Trova tutti gli export per un ordine specifico
     */
    List<ZonWizardExport> findByOrderIdOrderByLastExportAttemptDesc(Long orderId);

    /**
     * Trova export per stato
     */
    List<ZonWizardExport> findByExportStatus(String exportStatus);

    /**
     * Conta ordini esportati con successo
     */
    long countByExportStatus(String exportStatus);

    /**
     * Verifica se un ordine è già stato esportato con successo
     */
    boolean existsByOrderIdAndExportStatus(Long orderId, String exportStatus);

    /**
     * Trova export falliti con possibilità di retry
     */
    @Query("SELECT e FROM ZonWizardExport e WHERE e.exportStatus = 'FAILED' AND e.retryCount < :maxRetries")
    List<ZonWizardExport> findFailedExportsForRetry(@Param("maxRetries") int maxRetries);

    /**
     * Trova export per periodo
     */
    List<ZonWizardExport> findByExportSuccessDateBetween(LocalDateTime start, LocalDateTime end);

    /**
     * Trova export recenti (ultimi N giorni)
     */
    @Query("SELECT e FROM ZonWizardExport e WHERE e.lastExportAttempt >= :fromDate ORDER BY e.lastExportAttempt DESC")
    List<ZonWizardExport> findRecentExports(@Param("fromDate") LocalDateTime fromDate);

    /**
     * Trova export da ri-esportare (falliti recentemente)
     */
    @Query("SELECT e FROM ZonWizardExport e WHERE e.exportStatus = 'FAILED' " +
           "AND e.lastExportAttempt >= :fromDate " +
           "AND e.retryCount < :maxRetries " +
           "ORDER BY e.lastExportAttempt DESC")
    List<ZonWizardExport> findExportsToRetry(
        @Param("fromDate") LocalDateTime fromDate,
        @Param("maxRetries") int maxRetries
    );

    /**
     * Statistiche export per giorno
     */
    @Query("SELECT DATE(e.exportSuccessDate) as date, COUNT(e) as count " +
           "FROM ZonWizardExport e " +
           "WHERE e.exportStatus = 'SUCCESS' " +
           "AND e.exportSuccessDate >= :fromDate " +
           "GROUP BY DATE(e.exportSuccessDate) " +
           "ORDER BY DATE(e.exportSuccessDate) DESC")
    List<Object[]> getExportStatsByDate(@Param("fromDate") LocalDateTime fromDate);

    /**
     * Trova ordini non ancora esportati
     * (Join con tabella ordini per trovare quelli senza export)
     */
    @Query("SELECT o.id FROM WooCommerceOrder o " +
           "WHERE NOT EXISTS (" +
           "  SELECT 1 FROM ZonWizardExport e " +
           "  WHERE e.orderId = o.id AND e.exportStatus = 'SUCCESS'" +
           ")")
    List<Long> findOrderIdsNotExported();

    /**
     * Elimina vecchi record di export falliti (pulizia)
     */
    void deleteByExportStatusAndLastExportAttemptBefore(String exportStatus, LocalDateTime date);
}