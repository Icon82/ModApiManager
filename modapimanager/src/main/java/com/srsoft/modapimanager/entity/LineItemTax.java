package com.srsoft.modapimanager.entity;
 

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "line_item_taxes")
public class LineItemTax {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long autoId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "line_item_id")
    private LineItem lineItem;

    @JsonProperty("id")
    @Column(name = "tax_id")
    private Long id;

    @JsonProperty("total")
    @Column(precision = 10, scale = 6)
    private BigDecimal total;

    @JsonProperty("subtotal")
    @Column(precision = 10, scale = 6)
    private BigDecimal subtotal;
}