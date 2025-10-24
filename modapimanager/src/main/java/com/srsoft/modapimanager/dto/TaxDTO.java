package com.srsoft.modapimanager.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO per le tasse applicate ai singoli prodotti (line items)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaxDTO {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("total")
    private BigDecimal total;

    @JsonProperty("subtotal")
    private BigDecimal subtotal;
}