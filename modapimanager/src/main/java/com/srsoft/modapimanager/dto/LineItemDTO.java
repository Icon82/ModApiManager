package com.srsoft.modapimanager.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LineItemDTO {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("product_id")
    private Long productId;

    @JsonProperty("variation_id")
    private Long variationId;

    @JsonProperty("quantity")
    private Integer quantity;

    @JsonProperty("tax_class")
    private String taxClass;

    @JsonProperty("subtotal")
    private BigDecimal subtotal;

    @JsonProperty("subtotal_tax")
    private BigDecimal subtotalTax;

    @JsonProperty("total")
    private BigDecimal total;

    @JsonProperty("total_tax")
    private BigDecimal totalTax;

    @JsonProperty("sku")
    private String sku;

    @JsonProperty("price")
    private BigDecimal price;

    @JsonProperty("parent_name")
    private String parentName;


    @JsonProperty("taxes")
    private List<TaxDTO> taxes;

    @JsonProperty("meta_data")
    private List<MetaDataDTO> metaData;
}