package com.srsoft.modapimanager.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * DTO per le linee delle tasse dell'ordine
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaxLineDTO {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("rate_code")
    private String rateCode;

    @JsonProperty("rate_id")
    private Long rateId;

    @JsonProperty("label")
    private String label;

    @JsonProperty("compound")
    private Boolean compound;

    @JsonProperty("tax_total")
    private BigDecimal taxTotal;

    @JsonProperty("shipping_tax_total")
    private BigDecimal shippingTaxTotal;

    @JsonProperty("rate_percent")
    private Integer ratePercent;

    @JsonProperty("meta_data")
    private List<MetaDataDTO> metaData;
}