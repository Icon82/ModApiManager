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
@Table(name = "order_meta_data")
public class OrderMetaData {

    @Id
    @JsonProperty("id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private WooCommerceOrder order;

    @JsonProperty("key")
    @Column(name = "meta_key", length = 255)
    private String key;

    @JsonProperty("value")
    @Column(name = "meta_value", columnDefinition = "TEXT")
    private String value;
}