package com.srsoft.modapimanager.mapper;

import com.srsoft.modapimanager.dto.*;
import com.srsoft.modapimanager.entity.*;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper per convertire tra DTO e Entity
 */
@Component
public class OrderMapper {

    /**
     * Converte OrderDTO (da WooCommerce API) a WooCommerceOrder Entity (per DB)
     */
    public WooCommerceOrder toEntity(OrderDTO dto) {
        if (dto == null) {
            return null;
        }

        WooCommerceOrder entity = new WooCommerceOrder();
        
        // Campi semplici
        entity.setId(dto.getId());
        entity.setParentId(dto.getParentId());
        entity.setStatus(dto.getStatus());
        entity.setCurrency(dto.getCurrency());
        entity.setVersion(dto.getVersion());
        entity.setPricesIncludeTax(dto.getPricesIncludeTax());
        entity.setDateCreated(dto.getDateCreated());
        entity.setDateModified(dto.getDateModified());
        entity.setDiscountTotal(dto.getDiscountTotal());
        entity.setDiscountTax(dto.getDiscountTax());
        entity.setShippingTotal(dto.getShippingTotal());
        entity.setShippingTax(dto.getShippingTax());
        entity.setCartTax(dto.getCartTax());
        entity.setTotal(dto.getTotal());
        entity.setTotalTax(dto.getTotalTax());
        entity.setCustomerId(dto.getCustomerId());
        entity.setOrderKey(dto.getOrderKey());
        entity.setPaymentMethod(dto.getPaymentMethod());
        entity.setPaymentMethodTitle(dto.getPaymentMethodTitle());
        entity.setTransactionId(dto.getTransactionId());
        entity.setCustomerIpAddress(dto.getCustomerIpAddress());
        entity.setCustomerUserAgent(dto.getCustomerUserAgent());
        entity.setCreatedVia(dto.getCreatedVia());
        entity.setCustomerNote(dto.getCustomerNote());
        entity.setDateCompleted(dto.getDateCompleted());
        entity.setDatePaid(dto.getDatePaid());
        entity.setCartHash(dto.getCartHash());
        entity.setNumber(dto.getNumber());
        entity.setEmail(dto.getEmail());
        entity.setFinalAmount(dto.getFinalAmount());
        entity.setCurrencySymbol(dto.getCurrencySymbol());
        entity.setPaymentUrl(dto.getPaymentUrl());
        entity.setIsEditable(dto.getIsEditable());
        entity.setNeedsPayment(dto.getNeedsPayment());
        entity.setNeedsProcessing(dto.getNeedsProcessing());
        
        // Indirizzi
        entity.setBilling(toBillingAddress(dto.getBilling()));
        entity.setShipping(toShippingAddress(dto.getShipping()));
        
        // Relazioni (con gestione bidirezionale)
        if (dto.getLineItems() != null) {
            List<LineItem> lineItems = dto.getLineItems().stream()
                .map(this::toLineItemEntity)
                .collect(Collectors.toList());
            lineItems.forEach(item -> item.setOrder(entity));
            entity.setLineItems(lineItems);
        }
        
        if (dto.getTaxLines() != null) {
            List<TaxLine> taxLines = dto.getTaxLines().stream()
                .map(this::toTaxLineEntity)
                .collect(Collectors.toList());
            taxLines.forEach(tax -> tax.setOrder(entity));
            entity.setTaxLines(taxLines);
        }
        
        if (dto.getShippingLines() != null) {
            List<ShippingLine> shippingLines = dto.getShippingLines().stream()
                .map(this::toShippingLineEntity)
                .collect(Collectors.toList());
            shippingLines.forEach(shipping -> shipping.setOrder(entity));
            entity.setShippingLines(shippingLines);
        }
        
        if (dto.getMetaData() != null) {
            List<OrderMetaData> metaData = dto.getMetaData().stream()
                .map(this::toOrderMetaDataEntity)
                .collect(Collectors.toList());
            metaData.forEach(meta -> meta.setOrder(entity));
            entity.setMetaData(metaData);
        }
        
        return entity;
    }

    /**
     * Converte WooCommerceOrder Entity (da DB) a OrderDTO (per API response)
     */
    public OrderDTO toDTO(WooCommerceOrder entity) {
        if (entity == null) {
            return null;
        }

        OrderDTO dto = new OrderDTO();
        
        // Campi semplici
        dto.setId(entity.getId());
        dto.setParentId(entity.getParentId());
        dto.setStatus(entity.getStatus());
        dto.setCurrency(entity.getCurrency());
        dto.setVersion(entity.getVersion());
        dto.setPricesIncludeTax(entity.getPricesIncludeTax());
        dto.setDateCreated(entity.getDateCreated());
        dto.setDateModified(entity.getDateModified());
        dto.setDiscountTotal(entity.getDiscountTotal());
        dto.setDiscountTax(entity.getDiscountTax());
        dto.setShippingTotal(entity.getShippingTotal());
        dto.setShippingTax(entity.getShippingTax());
        dto.setCartTax(entity.getCartTax());
        dto.setTotal(entity.getTotal());
        dto.setTotalTax(entity.getTotalTax());
        dto.setCustomerId(entity.getCustomerId());
        dto.setOrderKey(entity.getOrderKey());
        dto.setPaymentMethod(entity.getPaymentMethod());
        dto.setPaymentMethodTitle(entity.getPaymentMethodTitle());
        dto.setTransactionId(entity.getTransactionId());
        dto.setCustomerIpAddress(entity.getCustomerIpAddress());
        dto.setCustomerUserAgent(entity.getCustomerUserAgent());
        dto.setCreatedVia(entity.getCreatedVia());
        dto.setCustomerNote(entity.getCustomerNote());
        dto.setDateCompleted(entity.getDateCompleted());
        dto.setDatePaid(entity.getDatePaid());
        dto.setCartHash(entity.getCartHash());
        dto.setNumber(entity.getNumber());
        dto.setEmail(entity.getEmail());
        dto.setFinalAmount(entity.getFinalAmount());
        dto.setCurrencySymbol(entity.getCurrencySymbol());
        dto.setPaymentUrl(entity.getPaymentUrl());
        dto.setIsEditable(entity.getIsEditable());
        dto.setNeedsPayment(entity.getNeedsPayment());
        dto.setNeedsProcessing(entity.getNeedsProcessing());
        
        // Indirizzi
        dto.setBilling(toBillingAddressDTO(entity.getBilling()));
        dto.setShipping(toShippingAddressDTO(entity.getShipping()));
        
        // Relazioni
        if (entity.getLineItems() != null) {
            dto.setLineItems(entity.getLineItems().stream()
                .map(this::toLineItemDTO)
                .collect(Collectors.toList()));
        }
        
        if (entity.getTaxLines() != null) {
            dto.setTaxLines(entity.getTaxLines().stream()
                .map(this::toTaxLineDTO)
                .collect(Collectors.toList()));
        }
        
        if (entity.getShippingLines() != null) {
            dto.setShippingLines(entity.getShippingLines().stream()
                .map(this::toShippingLineDTO)
                .collect(Collectors.toList()));
        }
        
        if (entity.getMetaData() != null) {
            dto.setMetaData(entity.getMetaData().stream()
                .map(this::toMetaDataDTO)
                .collect(Collectors.toList()));
        }
        
        return dto;
    }

    // Metodi helper per indirizzi
    private BillingAddress toBillingAddress(BillingAddressDTO dto) {
        if (dto == null) return null;
        BillingAddress address = new BillingAddress();
        address.setFirstName(dto.getFirstName());
        address.setLastName(dto.getLastName());
        address.setCompany(dto.getCompany());
        address.setAddress1(dto.getAddress1());
        address.setAddress2(dto.getAddress2());
        address.setCity(dto.getCity());
        address.setState(dto.getState());
        address.setPostcode(dto.getPostcode());
        address.setCountry(dto.getCountry());
        address.setEmail(dto.getEmail());
        address.setPhone(dto.getPhone());
        return address;
    }

    private BillingAddressDTO toBillingAddressDTO(BillingAddress entity) {
        if (entity == null) return null;
        BillingAddressDTO dto = new BillingAddressDTO();
        dto.setFirstName(entity.getFirstName());
        dto.setLastName(entity.getLastName());
        dto.setCompany(entity.getCompany());
        dto.setAddress1(entity.getAddress1());
        dto.setAddress2(entity.getAddress2());
        dto.setCity(entity.getCity());
        dto.setState(entity.getState());
        dto.setPostcode(entity.getPostcode());
        dto.setCountry(entity.getCountry());
        dto.setEmail(entity.getEmail());
        dto.setPhone(entity.getPhone());
        return dto;
    }

    private ShippingAddress toShippingAddress(ShippingAddressDTO dto) {
        if (dto == null) return null;
        ShippingAddress address = new ShippingAddress();
        address.setFirstName(dto.getFirstName());
        address.setLastName(dto.getLastName());
        address.setCompany(dto.getCompany());
        address.setAddress1(dto.getAddress1());
        address.setAddress2(dto.getAddress2());
        address.setCity(dto.getCity());
        address.setState(dto.getState());
        address.setPostcode(dto.getPostcode());
        address.setCountry(dto.getCountry());
        address.setPhone(dto.getPhone());
        return address;
    }

    private ShippingAddressDTO toShippingAddressDTO(ShippingAddress entity) {
        if (entity == null) return null;
        ShippingAddressDTO dto = new ShippingAddressDTO();
        dto.setFirstName(entity.getFirstName());
        dto.setLastName(entity.getLastName());
        dto.setCompany(entity.getCompany());
        dto.setAddress1(entity.getAddress1());
        dto.setAddress2(entity.getAddress2());
        dto.setCity(entity.getCity());
        dto.setState(entity.getState());
        dto.setPostcode(entity.getPostcode());
        dto.setCountry(entity.getCountry());
        dto.setPhone(entity.getPhone());
        return dto;
    }

    // Metodi helper per LineItem (semplificati - espandere se necessario)
    private LineItem toLineItemEntity(LineItemDTO dto) {
        LineItem entity = new LineItem();
        entity.setId(dto.getId());
        entity.setName(dto.getName());
        entity.setProductId(dto.getProductId());
        entity.setVariationId(dto.getVariationId());
        entity.setQuantity(dto.getQuantity());
        entity.setTaxClass(dto.getTaxClass());
        entity.setSubtotal(dto.getSubtotal());
        entity.setSubtotalTax(dto.getSubtotalTax());
        entity.setTotal(dto.getTotal());
        entity.setTotalTax(dto.getTotalTax());
        entity.setSku(dto.getSku());
        entity.setPrice(dto.getPrice());
        entity.setParentName(dto.getParentName());
        return entity;
    }

    private LineItemDTO toLineItemDTO(LineItem entity) {
        LineItemDTO dto = new LineItemDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setProductId(entity.getProductId());
        dto.setVariationId(entity.getVariationId());
        dto.setQuantity(entity.getQuantity());
        dto.setTaxClass(entity.getTaxClass());
        dto.setSubtotal(entity.getSubtotal());
        dto.setSubtotalTax(entity.getSubtotalTax());
        dto.setTotal(entity.getTotal());
        dto.setTotalTax(entity.getTotalTax());
        dto.setSku(entity.getSku());
        dto.setPrice(entity.getPrice());
        dto.setParentName(entity.getParentName());
        return dto;
    }

    private TaxLine toTaxLineEntity(TaxLineDTO dto) {
        TaxLine entity = new TaxLine();
        entity.setId(dto.getId());
        entity.setRateCode(dto.getRateCode());
        entity.setRateId(dto.getRateId());
        entity.setLabel(dto.getLabel());
        entity.setCompound(dto.getCompound());
        entity.setTaxTotal(dto.getTaxTotal());
        entity.setShippingTaxTotal(dto.getShippingTaxTotal());
        entity.setRatePercent(dto.getRatePercent());
        return entity;
    }

    private TaxLineDTO toTaxLineDTO(TaxLine entity) {
        TaxLineDTO dto = new TaxLineDTO();
        dto.setId(entity.getId());
        dto.setRateCode(entity.getRateCode());
        dto.setRateId(entity.getRateId());
        dto.setLabel(entity.getLabel());
        dto.setCompound(entity.getCompound());
        dto.setTaxTotal(entity.getTaxTotal());
        dto.setShippingTaxTotal(entity.getShippingTaxTotal());
        dto.setRatePercent(entity.getRatePercent());
        return dto;
    }

    private ShippingLine toShippingLineEntity(ShippingLineDTO dto) {
        ShippingLine entity = new ShippingLine();
        entity.setId(dto.getId());
        entity.setMethodTitle(dto.getMethodTitle());
        entity.setMethodId(dto.getMethodId());
        entity.setInstanceId(dto.getInstanceId());
        entity.setTotal(dto.getTotal());
        entity.setTotalTax(dto.getTotalTax());
        entity.setTaxStatus(dto.getTaxStatus());
        return entity;
    }

    private ShippingLineDTO toShippingLineDTO(ShippingLine entity) {
        ShippingLineDTO dto = new ShippingLineDTO();
        dto.setId(entity.getId());
        dto.setMethodTitle(entity.getMethodTitle());
        dto.setMethodId(entity.getMethodId());
        dto.setInstanceId(entity.getInstanceId());
        dto.setTotal(entity.getTotal());
        dto.setTotalTax(entity.getTotalTax());
        dto.setTaxStatus(entity.getTaxStatus());
        return dto;
    }

    private OrderMetaData toOrderMetaDataEntity(MetaDataDTO dto) {
        OrderMetaData entity = new OrderMetaData();
        entity.setId(dto.getId());
        entity.setKey(dto.getKey());
        entity.setValue(dto.getValue() != null ? dto.getValue().toString() : null);
        return entity;
    }

    private MetaDataDTO toMetaDataDTO(OrderMetaData entity) {
        MetaDataDTO dto = new MetaDataDTO();
        dto.setId(entity.getId());
        dto.setKey(entity.getKey());
        dto.setValue(entity.getValue());
        return dto;
    }

    /**
     * Converte lista di DTO a lista di Entity
     */
    public List<WooCommerceOrder> toEntityList(List<OrderDTO> dtoList) {
        if (dtoList == null) {
            return new ArrayList<>();
        }
        return dtoList.stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }

    /**
     * Converte lista di Entity a lista di DTO
     */
    public List<OrderDTO> toDTOList(List<WooCommerceOrder> entityList) {
        if (entityList == null) {
            return new ArrayList<>();
        }
        return entityList.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
}