package com.srsoft.modapimanager.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "woocommerce_orders")
public class WooCommerceOrder {

    @Id
    @JsonProperty("id")
    private Long id;

    @JsonProperty("parent_id")
    @Column(name = "parent_id")
    private Long parentId;

    @JsonProperty("status")
    @Column(length = 50)
    private String status;

    @JsonProperty("currency")
    @Column(length = 10)
    private String currency;

    @JsonProperty("version")
    @Column(length = 20)
    private String version;

    @JsonProperty("prices_include_tax")
    @Column(name = "prices_include_tax")
    private Boolean pricesIncludeTax;

    @JsonProperty("date_created")
    @Column(name = "date_created")
    private LocalDateTime dateCreated;

    @JsonProperty("date_modified")
    @Column(name = "date_modified")
    private LocalDateTime dateModified;

    @JsonProperty("discount_total")
    @Column(name = "discount_total", precision = 10, scale = 2)
    private BigDecimal discountTotal;

    @JsonProperty("discount_tax")
    @Column(name = "discount_tax", precision = 10, scale = 2)
    private BigDecimal discountTax;

    @JsonProperty("shipping_total")
    @Column(name = "shipping_total", precision = 10, scale = 2)
    private BigDecimal shippingTotal;

    @JsonProperty("shipping_tax")
    @Column(name = "shipping_tax", precision = 10, scale = 2)
    private BigDecimal shippingTax;

    @JsonProperty("cart_tax")
    @Column(name = "cart_tax", precision = 10, scale = 2)
    private BigDecimal cartTax;

    @JsonProperty("total")
    @Column(precision = 10, scale = 2)
    private BigDecimal total;

    @JsonProperty("total_tax")
    @Column(name = "total_tax", precision = 10, scale = 2)
    private BigDecimal totalTax;

    @JsonProperty("customer_id")
    @Column(name = "customer_id")
    private Long customerId;

    @JsonProperty("order_key")
    @Column(name = "order_key", length = 100)
    private String orderKey;

    @Embedded
    @JsonProperty("billing")
    private BillingAddress billing;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "firstName", column = @Column(name = "shipping_first_name")),
        @AttributeOverride(name = "lastName", column = @Column(name = "shipping_last_name")),
        @AttributeOverride(name = "company", column = @Column(name = "shipping_company")),
        @AttributeOverride(name = "address1", column = @Column(name = "shipping_address_1")),
        @AttributeOverride(name = "address2", column = @Column(name = "shipping_address_2")),
        @AttributeOverride(name = "city", column = @Column(name = "shipping_city")),
        @AttributeOverride(name = "state", column = @Column(name = "shipping_state")),
        @AttributeOverride(name = "postcode", column = @Column(name = "shipping_postcode")),
        @AttributeOverride(name = "country", column = @Column(name = "shipping_country")),
        @AttributeOverride(name = "phone", column = @Column(name = "shipping_phone"))
    })
    @JsonProperty("shipping")
    private ShippingAddress shipping;

    @JsonProperty("payment_method")
    @Column(name = "payment_method", length = 100)
    private String paymentMethod;

    @JsonProperty("payment_method_title")
    @Column(name = "payment_method_title", length = 100)
    private String paymentMethodTitle;

    @JsonProperty("transaction_id")
    @Column(name = "transaction_id", length = 100)
    private String transactionId;

    @JsonProperty("customer_ip_address")
    @Column(name = "customer_ip_address", length = 50)
    private String customerIpAddress;

    @JsonProperty("customer_user_agent")
    @Column(name = "customer_user_agent", length = 500)
    private String customerUserAgent;

    @JsonProperty("created_via")
    @Column(name = "created_via", length = 50)
    private String createdVia;

    @JsonProperty("customer_note")
    @Column(name = "customer_note", columnDefinition = "TEXT")
    private String customerNote;

    @JsonProperty("date_completed")
    @Column(name = "date_completed")
    private LocalDateTime dateCompleted;

    @JsonProperty("date_paid")
    @Column(name = "date_paid")
    private LocalDateTime datePaid;

    @JsonProperty("cart_hash")
    @Column(name = "cart_hash", length = 100)
    private String cartHash;

    @JsonProperty("number")
    @Column(length = 50)
    private String number;

    @JsonProperty("email")
    @Column(length = 100)
    private String email;

    @JsonProperty("final_amount")
    @Column(name = "final_amount")
    private String finalAmount;

    @JsonProperty("currency_symbol")
    @Column(name = "currency_symbol", length = 10)
    private String currencySymbol;

    @JsonProperty("payment_url")
    @Column(name = "payment_url", length = 500)
    private String paymentUrl;

    @JsonProperty("is_editable")
    @Column(name = "is_editable")
    private Boolean isEditable;

    @JsonProperty("needs_payment")
    @Column(name = "needs_payment")
    private Boolean needsPayment;

    @JsonProperty("needs_processing")
    @Column(name = "needs_processing")
    private Boolean needsProcessing;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonProperty("line_items")
    private List<LineItem> lineItems;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonProperty("tax_lines")
    private List<TaxLine> taxLines;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonProperty("shipping_lines")
    private List<ShippingLine> shippingLines;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonProperty("meta_data")
    private List<OrderMetaData> metaData;
}