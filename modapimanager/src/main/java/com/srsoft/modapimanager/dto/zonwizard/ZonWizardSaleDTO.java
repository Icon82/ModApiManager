package com.srsoft.modapimanager.dto.zonwizard;
 

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO per esportare ordini verso ZonWizard API
 * 
 * Questo DTO rappresenta una vendita da inviare a ZonWizard.
 * I campi possono essere adattati in base alla documentazione API ufficiale.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ZonWizardSaleDTO {

    /**
     * ID univoco dell'ordine (proveniente da WooCommerce)
     */
    @JsonProperty("order_id")
    private String orderId;

    /**
     * Numero ordine leggibile (esempio: "27978")
     */
    @JsonProperty("order_number")
    private String orderNumber;

    /**
     * Data e ora dell'ordine
     */
    @JsonProperty("order_date")
    private LocalDateTime orderDate;

    /**
     * Stato dell'ordine (processing, completed, etc.)
     */
    @JsonProperty("status")
    private String status;

    /**
     * Canale di vendita (es: "WooCommerce", "Shopify", "Custom")
     */
    @JsonProperty("channel")
    private String channel;

    /**
     * Marketplace/paese (es: "IT", "FR", "DE")
     */
    @JsonProperty("marketplace")
    private String marketplace;

    /**
     * Totale ordine (incluse tasse)
     */
    @JsonProperty("total")
    private BigDecimal total;

    /**
     * Subtotale (escluse tasse e spedizione)
     */
    @JsonProperty("subtotal")
    private BigDecimal subtotal;

    /**
     * Totale tasse
     */
    @JsonProperty("total_tax")
    private BigDecimal totalTax;

    /**
     * Totale spedizione
     */
    @JsonProperty("shipping_total")
    private BigDecimal shippingTotal;

    /**
     * Tasse sulla spedizione
     */
    @JsonProperty("shipping_tax")
    private BigDecimal shippingTax;

    /**
     * Totale sconti applicati
     */
    @JsonProperty("discount_total")
    private BigDecimal discountTotal;

    /**
     * Valuta (ISO 4217: EUR, USD, GBP, etc.)
     */
    @JsonProperty("currency")
    private String currency;

    /**
     * Metodo di pagamento
     */
    @JsonProperty("payment_method")
    private String paymentMethod;

    /**
     * Email cliente
     */
    @JsonProperty("customer_email")
    private String customerEmail;

    /**
     * Nome cliente
     */
    @JsonProperty("customer_name")
    private String customerName;

    /**
     * Paese di fatturazione (ISO code: IT, FR, DE, etc.)
     */
    @JsonProperty("billing_country")
    private String billingCountry;

    /**
     * Paese di spedizione (ISO code: IT, FR, DE, etc.)
     */
    @JsonProperty("shipping_country")
    private String shippingCountry;

    /**
     * Partita IVA cliente (se B2B)
     */
    @JsonProperty("vat_number")
    private String vatNumber;

    /**
     * Indica se è una vendita B2B
     */
    @JsonProperty("is_business")
    private Boolean isBusiness;

    /**
     * Prodotti/linee dell'ordine
     */
    @JsonProperty("line_items")
    private List<ZonWizardLineItemDTO> lineItems;

    /**
     * Metodo di spedizione
     */
    @JsonProperty("shipping_method")
    private String shippingMethod;

    /**
     * Note aggiuntive
     */
    @JsonProperty("notes")
    private String notes;

    /**
     * Metadati personalizzati (opzionale)
     */
    @JsonProperty("metadata")
    private Object metadata;
}
