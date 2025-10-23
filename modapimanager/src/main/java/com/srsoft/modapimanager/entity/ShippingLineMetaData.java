package com.srsoft.modapimanager.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import jakarta.persistence.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "shipping_line_meta_data")
public class ShippingLineMetaData {

    @Id
    @JsonProperty("id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shipping_line_id")
    private ShippingLine shippingLine;

    @JsonProperty("key")
    @Column(name = "meta_key", length = 255)
    private String key;

    @JsonProperty("value")
    @Column(name = "meta_value", columnDefinition = "TEXT")
    private String value;

    @JsonProperty("display_key")
    @Column(name = "display_key", length = 255)
    private String displayKey;

    @JsonProperty("display_value")
    @Column(name = "display_value", columnDefinition = "TEXT")
    private String displayValue;
}