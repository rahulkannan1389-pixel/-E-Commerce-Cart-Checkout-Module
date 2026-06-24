package com.techpalle.serviceimpl;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.techpalle.dto.response.OrderDTO;
import com.techpalle.dto.response.OrderItemDTO;
import com.techpalle.entity.Order;
import com.techpalle.entity.OrderItem;
import com.techpalle.repository.OrderRepository;
import com.techpalle.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.techpalle.exception.ResourceNotFoundException;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderServiceImpl implements OrderService {

   private final OrderRepository orderRepository;

    @Override
    public Page<OrderDTO> getOrderHistory(Long userId, Pageable pageable) {
    	
        log.info("Fetching order history for user {}", userId);
        
        Page<Order> orders = orderRepository.findByUserId(userId, pageable);
        
        log.debug("Fetched {} orders for user {}", 
                orders.getTotalElements(), userId);

        return orders.map(this::mapOrderToDTO);
    }

    @Override
    public OrderDTO getOrderById(Long orderId) {

        log.info("Fetching order by ID {}", orderId);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> {
                    log.error("Order not found: {}", orderId);
                    return new ResourceNotFoundException("Order not found");
                });

        log.debug("Order {} fetched successfully", orderId);

        return mapOrderToDTO(order);
    }

    private OrderDTO mapOrderToDTO(Order order) {

        log.debug("Mapping order {} to DTO", order.getId());

        List<OrderItemDTO> items = order.getOrderItems().stream()
                .map(this::mapOrderItemToDTO)
                .toList();

        return OrderDTO.builder()
                .id(order.getId())
                .userId(order.getUser().getId())
                .totalAmount(order.getTotalAmount())
                .status(order.getStatus())
                .createdAt(order.getCreatedAt())
                .items(items)
                .build();
    }

    private OrderItemDTO mapOrderItemToDTO(OrderItem orderItem) {

        return OrderItemDTO.builder()
                .id(orderItem.getId())
                .productId(orderItem.getProduct().getId())
                .productName(orderItem.getProduct().getName())
                .quantity(orderItem.getQuantity())
                .price(orderItem.getPrice())
                .build();
    }
}
