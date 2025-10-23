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
@Table(name = "shipping_lines")
public class ShippingLine {

    @Id
    @JsonProperty("id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private WooCommerceOrder order;

    @JsonProperty("method_title")
    @Column(name = "method_title", length = 200)
    private String methodTitle;

    @JsonProperty("method_id")
    @Column(name = "method_id", length = 100)
    private String methodId;

    @JsonProperty("instance_id")
    @Column(name = "instance_id", length = 50)
    private String instanceId;

    @JsonProperty("total")
    @Column(precision = 10, scale = 2)
    private BigDecimal total;

    @JsonProperty("total_tax")
    @Column(name = "total_tax", precision = 10, scale = 2)
    private BigDecimal totalTax;

    @JsonProperty("tax_status")
    @Column(name = "tax_status", length = 50)
    private String taxStatus;

    @OneToMany(mappedBy = "shippingLine", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonProperty("meta_data")
    private List<ShippingLineMetaData> metaData;
}