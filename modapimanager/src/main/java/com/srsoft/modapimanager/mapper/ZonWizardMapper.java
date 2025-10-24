package com.srsoft.modapimanager.mapper;

import com.srsoft.modapimanager.dto.zonwizard.ZonWizardLineItemDTO;
import com.srsoft.modapimanager.dto.zonwizard.ZonWizardSaleDTO;
import com.srsoft.modapimanager.entity.LineItem;
import com.srsoft.modapimanager.entity.OrderMetaData;
import com.srsoft.modapimanager.entity.WooCommerceOrder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper CORRETTO per convertire ordini WooCommerce in formato ZonWizard
 * Allineato con l'entity WooCommerceOrder reale
 */
@Component
public class ZonWizardMapper {

    /**
     * Converte un ordine WooCommerce in ZonWizardSaleDTO
     */
    public ZonWizardSaleDTO toZonWizardSale(WooCommerceOrder order) {
        if (order == null) {
            return null;
        }

        return ZonWizardSaleDTO.builder()
                .orderId(order.getId().toString())
                .orderNumber(order.getNumber())
                .orderDate(order.getDateCreated())
                .status(order.getStatus())
                .channel("WooCommerce")
                .marketplace(determineMarketplace(order))
                .total(order.getTotal())
                .subtotal(calculateSubtotal(order)) // ✅ CALCOLATO
                .totalTax(order.getTotalTax())
                .shippingTotal(order.getShippingTotal())
                .shippingTax(order.getShippingTax())
                .discountTotal(order.getDiscountTotal())
                .currency(order.getCurrency())
                .paymentMethod(order.getPaymentMethod())
                .customerEmail(extractCustomerEmail(order)) // ✅ CORRETTO
                .customerName(buildCustomerName(order))
                .billingCountry(order.getBilling() != null ? 
                    order.getBilling().getCountry() : null) // ✅ CORRETTO
                .shippingCountry(order.getShipping() != null ? 
                    order.getShipping().getCountry() : null) // ✅ CORRETTO
                .vatNumber(extractVatNumber(order))
                .isBusiness(isBusinessOrder(order))
                .lineItems(mapLineItems(order.getLineItems()))
                .shippingMethod(extractShippingMethod(order))
                .notes(order.getCustomerNote())
                .build();
    }

    /**
     * ✅ NUOVO: Calcola il subtotal dall'ordine
     * subtotal = total - shipping_total - shipping_tax + discount_total
     */
    private BigDecimal calculateSubtotal(WooCommerceOrder order) {
        BigDecimal total = order.getTotal() != null ? order.getTotal() : BigDecimal.ZERO;
        BigDecimal shippingTotal = order.getShippingTotal() != null ? order.getShippingTotal() : BigDecimal.ZERO;
        BigDecimal shippingTax = order.getShippingTax() != null ? order.getShippingTax() : BigDecimal.ZERO;
        BigDecimal discountTotal = order.getDiscountTotal() != null ? order.getDiscountTotal() : BigDecimal.ZERO;
        
        // subtotal = total - shipping - shipping_tax + discount
        return total
                .subtract(shippingTotal)
                .subtract(shippingTax)
                .add(discountTotal);
    }

    /**
     * ✅ CORRETTO: Estrae email dal billing (campo corretto)
     */
    private String extractCustomerEmail(WooCommerceOrder order) {
        // Priorità: billing.email > order.email
        if (order.getBilling() != null && order.getBilling().getEmail() != null) {
            return order.getBilling().getEmail().toLowerCase().trim();
        }
        if (order.getEmail() != null) {
            return order.getEmail().toLowerCase().trim();
        }
        return null;
    }

    /**
     * ✅ CORRETTO: Usa getBilling() invece di getBillingAddress()
     */
    private String buildCustomerName(WooCommerceOrder order) {
        if (order.getBilling() == null) {
            return null;
        }
        
        String firstName = order.getBilling().getFirstName();
        String lastName = order.getBilling().getLastName();
        String company = order.getBilling().getCompany();
        
        // Se c'è azienda, priorità all'azienda
        if (company != null && !company.trim().isEmpty()) {
            return company.trim();
        }
        
        // Altrimenti nome completo
        if (firstName != null && lastName != null) {
            return (firstName + " " + lastName).trim();
        }
        return firstName != null ? firstName.trim() : 
               (lastName != null ? lastName.trim() : null);
    }

    /**
     * ✅ MIGLIORATO: Estrae P.IVA dai metadati con 8 chiavi diverse
     */
    private String extractVatNumber(WooCommerceOrder order) {
        if (order.getMetaData() == null || order.getMetaData().isEmpty()) {
            return null;
        }

        // Chiavi comuni per P.IVA in plugin italiani/europei
        String[] vatKeys = {
            "_billing_vat_number",
            "vat_number",
            "_vat_number",
            "partita_iva",
            "_billing_partita_iva",
            "cf_piva",
            "_billing_cf_piva",
            "VAT Number"
        };

        for (String key : vatKeys) {
            for (OrderMetaData meta : order.getMetaData()) {
                if (key.equalsIgnoreCase(meta.getKey()) && 
                    meta.getValue() != null && 
                    !meta.getValue().trim().isEmpty()) {
                    return cleanVatNumber(meta.getValue());
                }
            }
        }

        return null;
    }

    /**
     * ✅ NUOVO: Pulisce e normalizza la P.IVA
     * Rimuove spazi, punti, trattini
     */
    private String cleanVatNumber(String vat) {
        if (vat == null) {
            return null;
        }
        // Rimuove spazi, punti, trattini
        return vat.replaceAll("[\\s.-]", "").toUpperCase().trim();
    }

    /**
     * ✅ MIGLIORATO: Determina se è B2B controllando P.IVA E company
     */
    private Boolean isBusinessOrder(WooCommerceOrder order) {
        // B2B se ha P.IVA valida
        String vatNumber = extractVatNumber(order);
        if (vatNumber != null && vatNumber.length() >= 11) {
            return true;
        }

        // O se ha nome azienda compilato
        if (order.getBilling() != null && 
            order.getBilling().getCompany() != null && 
            !order.getBilling().getCompany().trim().isEmpty()) {
            return true;
        }

        return false;
    }

    /**
     * ✅ CORRETTO: Determina marketplace (getBilling/getShipping)
     */
    private String determineMarketplace(WooCommerceOrder order) {
        // Priorità: shipping > billing > default
        if (order.getShipping() != null && 
            order.getShipping().getCountry() != null) {
            return order.getShipping().getCountry().toUpperCase();
        }
        if (order.getBilling() != null && 
            order.getBilling().getCountry() != null) {
            return order.getBilling().getCountry().toUpperCase();
        }
        return "IT"; // Default
    }

    /**
     * Estrae metodo di spedizione
     */
    private String extractShippingMethod(WooCommerceOrder order) {
        if (order.getShippingLines() != null && !order.getShippingLines().isEmpty()) {
            return order.getShippingLines().get(0).getMethodTitle();
        }
        return null;
    }

    // ========== LINE ITEMS MAPPING ==========

    private List<ZonWizardLineItemDTO> mapLineItems(List<LineItem> lineItems) {
        if (lineItems == null || lineItems.isEmpty()) {
            return List.of();
        }

        return lineItems.stream()
                .map(this::mapLineItem)
                .collect(Collectors.toList());
    }

    private ZonWizardLineItemDTO mapLineItem(LineItem item) {
        return ZonWizardLineItemDTO.builder()
                .sku(item.getSku())
                .productId(item.getProductId())
                .productName(item.getName())
                .quantity(item.getQuantity())
                .unitPrice(calculateUnitPrice(item))
                .lineTotal(item.getSubtotal())
                .lineTax(item.getTotalTax())
                .taxRate(calculateTaxRate(item))
                .build();
    }

    private BigDecimal calculateUnitPrice(LineItem item) {
        if (item.getQuantity() == null || item.getQuantity() == 0) {
            return BigDecimal.ZERO;
        }
        BigDecimal subtotal = item.getSubtotal() != null ? item.getSubtotal() : BigDecimal.ZERO;
        return subtotal.divide(
            BigDecimal.valueOf(item.getQuantity()), 
            2, 
            RoundingMode.HALF_UP
        );
    }

    private BigDecimal calculateTaxRate(LineItem item) {
        BigDecimal subtotal = item.getSubtotal();
        BigDecimal tax = item.getTotalTax();
        
        if (subtotal == null || subtotal.compareTo(BigDecimal.ZERO) == 0 || tax == null) {
            return BigDecimal.ZERO;
        }

        // (tax / subtotal) * 100
        return tax.divide(subtotal, 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .setScale(2, RoundingMode.HALF_UP);
    }

    // ========== UTILITY ==========

    public List<ZonWizardSaleDTO> toZonWizardSaleList(List<WooCommerceOrder> orders) {
        if (orders == null || orders.isEmpty()) {
            return List.of();
        }
        return orders.stream()
                .map(this::toZonWizardSale)
                .collect(Collectors.toList());
    }
}