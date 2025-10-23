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
@Table(name = "order_line_items")
public class LineItem {

    @Id
    @JsonProperty("id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private WooCommerceOrder order;

    @JsonProperty("name")
    @Column(length = 500)
    private String name;

    @JsonProperty("product_id")
    @Column(name = "product_id")
    private Long productId;

    @JsonProperty("variation_id")
    @Column(name = "variation_id")
    private Long variationId;

    @JsonProperty("quantity")
    private Integer quantity;

    @JsonProperty("tax_class")
    @Column(name = "tax_class", length = 50)
    private String taxClass;

    @JsonProperty("subtotal")
    @Column(precision = 10, scale = 2)
    private BigDecimal subtotal;

    @JsonProperty("subtotal_tax")
    @Column(name = "subtotal_tax", precision = 10, scale = 2)
    private BigDecimal subtotalTax;

    @JsonProperty("total")
    @Column(precision = 10, scale = 2)
    private BigDecimal total;

    @JsonProperty("total_tax")
    @Column(name = "total_tax", precision = 10, scale = 2)
    private BigDecimal totalTax;

    @JsonProperty("sku")
    @Column(length = 100)
    private String sku;

    @JsonProperty("price")
    @Column(precision = 20, scale = 10)
    private BigDecimal price;

    @JsonProperty("parent_name")
    @Column(name = "parent_name", length = 500)
    private String parentName;


    @OneToMany(mappedBy = "lineItem", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonProperty("taxes")
    private List<LineItemTax> taxes;

    @OneToMany(mappedBy = "lineItem", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonProperty("meta_data")
    private List<LineItemMetaData> metaData;
}