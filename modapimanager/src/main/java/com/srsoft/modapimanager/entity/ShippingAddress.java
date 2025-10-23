package com.srsoft.modapimanager.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class ShippingAddress {

    @JsonProperty("first_name")
    @Column(name = "shipping_first_name", length = 100)
    private String firstName;

    @JsonProperty("last_name")
    @Column(name = "shipping_last_name", length = 100)
    private String lastName;

    @JsonProperty("company")
    @Column(name = "shipping_company", length = 200)
    private String company;

    @JsonProperty("address_1")
    @Column(name = "shipping_address_1", length = 200)
    private String address1;

    @JsonProperty("address_2")
    @Column(name = "shipping_address_2", length = 200)
    private String address2;

    @JsonProperty("city")
    @Column(name = "shipping_city", length = 100)
    private String city;

    @JsonProperty("state")
    @Column(name = "shipping_state", length = 50)
    private String state;

    @JsonProperty("postcode")
    @Column(name = "shipping_postcode", length = 20)
    private String postcode;

    @JsonProperty("country")
    @Column(name = "shipping_country", length = 10)
    private String country;

    @JsonProperty("phone")
    @Column(name = "shipping_phone", length = 50)
    private String phone;
}