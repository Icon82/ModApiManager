package com.srsoft.modapimanager.dto.zonwizard;



import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO per rappresentare un singolo prodotto/linea di un ordine verso ZonWizard
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ZonWizardLineItemDTO {

    /**
     * SKU del prodotto
     */
    @JsonProperty("sku")
    private String sku;

    /**
     * ID prodotto
     */
    @JsonProperty("product_id")
    private Long productId;

    /**
     * Nome del prodotto
     */
    @JsonProperty("product_name")
    private String productName;

    /**
     * Quantità ordinata
     */
    @JsonProperty("quantity")
    private Integer quantity;

    /**
     * Prezzo unitario (escluse tasse)
     */
    @JsonProperty("unit_price")
    private BigDecimal unitPrice;

    /**
     * Totale linea (escluse tasse)
     */
    @JsonProperty("line_total")
    private BigDecimal lineTotal;

    /**
     * Totale tasse sulla linea
     */
    @JsonProperty("line_tax")
    private BigDecimal lineTax;

    /**
     * Aliquota IVA applicata (esempio: 22 per 22%)
     */
    @JsonProperty("tax_rate")
    private BigDecimal taxRate;

    /**
     * Categoria prodotto
     */
    @JsonProperty("category")
    private String category;

    /**
     * Metadati aggiuntivi del prodotto
     */
    @JsonProperty("metadata")
    private Object metadata;
}