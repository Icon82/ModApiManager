package com.srsoft.modapimanager.service;

import com.srsoft.modapimanager.entity.WooCommerceOrder;
import com.srsoft.modapimanager.repository.OrderRepository;
 

import com.srsoft.modapimanager.dto.OrderDTO;
 
import com.srsoft.modapimanager.mapper.OrderMapper;
 
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class WooCommerceService {

    private static final Logger logger = LoggerFactory.getLogger(WooCommerceService.class);

    @Autowired
    @Qualifier("wooCommerceRestTemplate")
    private RestTemplate restTemplate;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderMapper orderMapper;

    /**
     * Recupera tutti gli ordini da WooCommerce e li salva nel database
     */
    @Transactional
    public List<OrderDTO> syncAllOrders() {
        logger.info("Inizio sincronizzazione ordini da WooCommerce...");
        
        // Chiamata API WooCommerce (riceve DTO)
        ResponseEntity<List<OrderDTO>> response = restTemplate.exchange(
                "/orders",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<OrderDTO>>() {}
        );
        
        List<OrderDTO> orderDTOs = response.getBody();
        
        if (orderDTOs != null && !orderDTOs.isEmpty()) {
            // Converte DTO → Entity
            List<WooCommerceOrder> entities = orderMapper.toEntityList(orderDTOs);
            
            // Salva nel database
            List<WooCommerceOrder> savedEntities = orderRepository.saveAll(entities);
            
            // Converte Entity → DTO per la risposta
            List<OrderDTO> savedDTOs = orderMapper.toDTOList(savedEntities);
            
            logger.info("Sincronizzati {} ordini nel database", savedDTOs.size());
            return savedDTOs;
        }
        
        logger.warn("Nessun ordine recuperato da WooCommerce");
        return List.of();
    }

    /**
     * Recupera ordini da WooCommerce per stato e li salva nel database
     */
    @Transactional
    public List<OrderDTO> syncOrdersByStatus(String status, int perPage, int page) {
        logger.info("Sincronizzazione ordini con stato: {}", status);
        
        String url = String.format("/orders?status=%s&per_page=%d&page=%d", 
                                    status, perPage, page);
        
        ResponseEntity<List<OrderDTO>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<OrderDTO>>() {}
        );
        
        List<OrderDTO> orderDTOs = response.getBody();
        
        if (orderDTOs != null && !orderDTOs.isEmpty()) {
            List<WooCommerceOrder> entities = orderMapper.toEntityList(orderDTOs);
            List<WooCommerceOrder> savedEntities = orderRepository.saveAll(entities);
            List<OrderDTO> savedDTOs = orderMapper.toDTOList(savedEntities);
            
            logger.info("Sincronizzati {} ordini con stato {}", savedDTOs.size(), status);
            return savedDTOs;
        }
        
        return List.of();
    }

    /**
     * Recupera un singolo ordine da WooCommerce e lo salva nel database
     */
    @Transactional
    public OrderDTO syncOrderById(Long orderId) {
        logger.info("Sincronizzazione ordine ID: {}", orderId);
        
        OrderDTO orderDTO = restTemplate.getForObject(
                "/orders/" + orderId, 
                OrderDTO.class
        );
        
        if (orderDTO != null) {
            WooCommerceOrder entity = orderMapper.toEntity(orderDTO);
            WooCommerceOrder savedEntity = orderRepository.save(entity);
            OrderDTO savedDTO = orderMapper.toDTO(savedEntity);
            
            logger.info("Ordine {} salvato nel database", orderId);
            return savedDTO;
        }
        
        logger.warn("Ordine {} non trovato su WooCommerce", orderId);
        return null;
    }

    /**
     * Recupera ordini recenti da WooCommerce e li salva
     */
    @Transactional
    public List<OrderDTO> syncRecentOrders(int limit) {
        logger.info("Sincronizzazione ultimi {} ordini", limit);
        
        String url = String.format("/orders?per_page=%d&orderby=date&order=desc", limit);
        
        ResponseEntity<List<OrderDTO>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<OrderDTO>>() {}
        );
        
        List<OrderDTO> orderDTOs = response.getBody();
        
        if (orderDTOs != null && !orderDTOs.isEmpty()) {
            List<WooCommerceOrder> entities = orderMapper.toEntityList(orderDTOs);
            List<WooCommerceOrder> savedEntities = orderRepository.saveAll(entities);
            List<OrderDTO> savedDTOs = orderMapper.toDTOList(savedEntities);
            
            logger.info("Sincronizzati {} ordini recenti", savedDTOs.size());
            return savedDTOs;
        }
        
        return List.of();
    }

    /**
     * Recupera tutti gli ordini dal DATABASE locale (non da WooCommerce)
     * Restituisce DTO per non esporre entity
     */
    public List<OrderDTO> getAllOrdersFromDB() {
        logger.info("Recupero tutti gli ordini dal database locale");
        List<WooCommerceOrder> entities = orderRepository.findAll();
        return orderMapper.toDTOList(entities);
    }

    /**
     * Recupera un ordine dal DATABASE locale
     * Restituisce DTO per non esporre entity
     */
    public OrderDTO getOrderFromDB(Long orderId) {
        logger.info("Recupero ordine {} dal database locale", orderId);
        return orderRepository.findById(orderId)
                .map(orderMapper::toDTO)
                .orElse(null);
    }

    /**
     * Recupera ordini per stato dal DATABASE locale
     * Restituisce DTO per non esporre entity
     */
    public List<OrderDTO> getOrdersByStatusFromDB(String status) {
        logger.info("Recupero ordini con stato {} dal database locale", status);
        List<WooCommerceOrder> entities = orderRepository.findByStatus(status);
        return orderMapper.toDTOList(entities);
    }

    /**
     * Aggiorna lo stato di un ordine su WooCommerce e nel database
     */
    @Transactional
    public OrderDTO updateOrderStatus(Long orderId, String newStatus) {
        logger.info("Aggiornamento stato ordine {} a {}", orderId, newStatus);
        
        String url = "/orders/" + orderId;
        OrderDTO updateData = new OrderDTO();
        updateData.setStatus(newStatus);
        
        // Aggiorna su WooCommerce
        OrderDTO updatedDTO = restTemplate.patchForObject(
                url, 
                updateData, 
                OrderDTO.class
        );
        
        // Salva nel database
        if (updatedDTO != null) {
            WooCommerceOrder entity = orderMapper.toEntity(updatedDTO);
            WooCommerceOrder savedEntity = orderRepository.save(entity);
            updatedDTO = orderMapper.toDTO(savedEntity);
            
            logger.info("Ordine {} aggiornato sia su WooCommerce che nel database", orderId);
        }
        
        return updatedDTO;
    }

    /**
     * Verifica se un ordine esiste nel database
     */
    public boolean orderExistsInDB(Long orderId) {
        return orderRepository.existsById(orderId);
    }

    /**
     * Elimina un ordine dal database locale (NON da WooCommerce)
     */
    @Transactional
    public void deleteOrderFromDB(Long orderId) {
        logger.info("Eliminazione ordine {} dal database locale", orderId);
        orderRepository.deleteById(orderId);
    }
}