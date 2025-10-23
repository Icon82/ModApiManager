package com.srsoft.modapimanager.entity;

 
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tax_lines")
public class TaxLine {

    @Id
    @JsonProperty("id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private WooCommerceOrder order;

    @JsonProperty("rate_code")
    @Column(name = "rate_code", length = 50)
    private String rateCode;

    @JsonProperty("rate_id")
    @Column(name = "rate_id")
    private Long rateId;

    @JsonProperty("label")
    @Column(length = 100)
    private String label;

    @JsonProperty("compound")
    private Boolean compound;

    @JsonProperty("tax_total")
    @Column(name = "tax_total", precision = 10, scale = 2)
    private BigDecimal taxTotal;

    @JsonProperty("shipping_tax_total")
    @Column(name = "shipping_tax_total", precision = 10, scale = 2)
    private BigDecimal shippingTaxTotal;

    @JsonProperty("rate_percent")
    @Column(name = "rate_percent")
    private Integer ratePercent;

    @OneToMany(mappedBy = "taxLine", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonProperty("meta_data")
    private List<TaxLineMetaData> metaData;
}