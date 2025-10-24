package com.srsoft.modapimanager.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO per ricevere ordini dall'API WooCommerce
 * e inviare risposte al client
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDTO {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("parent_id")
    private Long parentId;

    @JsonProperty("status")
    private String status;

    @JsonProperty("currency")
    private String currency;

    @JsonProperty("version")
    private String version;

    @JsonProperty("prices_include_tax")
    private Boolean pricesIncludeTax;

    @JsonProperty("date_created")
    private LocalDateTime dateCreated;

    @JsonProperty("date_modified")
    private LocalDateTime dateModified;

    @JsonProperty("discount_total")
    private BigDecimal discountTotal;

    @JsonProperty("discount_tax")
    private BigDecimal discountTax;

    @JsonProperty("shipping_total")
    private BigDecimal shippingTotal;

    @JsonProperty("shipping_tax")
    private BigDecimal shippingTax;

    @JsonProperty("cart_tax")
    private BigDecimal cartTax;

    @JsonProperty("total")
    private BigDecimal total;

    @JsonProperty("total_tax")
    private BigDecimal totalTax;

    @JsonProperty("customer_id")
    private Long customerId;

    @JsonProperty("order_key")
    private String orderKey;

    @JsonProperty("billing")
    private BillingAddressDTO billing;

    @JsonProperty("shipping")
    private ShippingAddressDTO shipping;

    @JsonProperty("payment_method")
    private String paymentMethod;

    @JsonProperty("payment_method_title")
    private String paymentMethodTitle;

    @JsonProperty("transaction_id")
    private String transactionId;

    @JsonProperty("customer_ip_address")
    private String customerIpAddress;

    @JsonProperty("customer_user_agent")
    private String customerUserAgent;

    @JsonProperty("created_via")
    private String createdVia;

    @JsonProperty("customer_note")
    private String customerNote;

    @JsonProperty("date_completed")
    private LocalDateTime dateCompleted;

    @JsonProperty("date_paid")
    private LocalDateTime datePaid;

    @JsonProperty("cart_hash")
    private String cartHash;

    @JsonProperty("number")
    private String number;

    @JsonProperty("email")
    private String email;

    @JsonProperty("final_amount")
    private String finalAmount;

    @JsonProperty("currency_symbol")
    private String currencySymbol;

    @JsonProperty("payment_url")
    private String paymentUrl;

    @JsonProperty("is_editable")
    private Boolean isEditable;

    @JsonProperty("needs_payment")
    private Boolean needsPayment;

    @JsonProperty("needs_processing")
    private Boolean needsProcessing;

    @JsonProperty("line_items")
    private List<LineItemDTO> lineItems;

    @JsonProperty("tax_lines")
    private List<TaxLineDTO> taxLines;

    @JsonProperty("shipping_lines")
    private List<ShippingLineDTO> shippingLines;

    @JsonProperty("meta_data")
    private List<MetaDataDTO> metaData;
}