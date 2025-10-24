package com.srsoft.modapimanager.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * DTO per le linee di spedizione dell'ordine
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShippingLineDTO {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("method_title")
    private String methodTitle;

    @JsonProperty("method_id")
    private String methodId;

    @JsonProperty("instance_id")
    private String instanceId;

    @JsonProperty("total")
    private BigDecimal total;

    @JsonProperty("total_tax")
    private BigDecimal totalTax;

    @JsonProperty("tax_status")
    private String taxStatus;

    @JsonProperty("meta_data")
    private List<MetaDataDTO> metaData;
}