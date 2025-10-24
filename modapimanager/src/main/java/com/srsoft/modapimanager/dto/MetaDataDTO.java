package com.srsoft.modapimanager.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * DTO per i metadati (ordini, prodotti, tasse, spedizione)
 * Campo "value" è Object per gestire diversi tipi (String, Number, JSON Object)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MetaDataDTO {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("key")
    private String key;

    @JsonProperty("value")
    private Object value;

    @JsonProperty("display_key")
    private String displayKey;

    @JsonProperty("display_value")
    private String displayValue;
}