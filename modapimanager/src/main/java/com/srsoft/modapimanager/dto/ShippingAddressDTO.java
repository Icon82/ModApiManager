package com.srsoft.modapimanager.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShippingAddressDTO {

    @JsonProperty("first_name")
    private String firstName;

    @JsonProperty("last_name")
    private String lastName;

    @JsonProperty("company")
    private String company;

    @JsonProperty("address_1")
    private String address1;

    @JsonProperty("address_2")
    private String address2;

    @JsonProperty("city")
    private String city;

    @JsonProperty("state")
    private String state;

    @JsonProperty("postcode")
    private String postcode;

    @JsonProperty("country")
    private String country;

    @JsonProperty("phone")
    private String phone;
}