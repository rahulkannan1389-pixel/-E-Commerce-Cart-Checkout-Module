package com.techpalle.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.techpalle.dto.response.OrderDTO;

public interface OrderService {

	    Page<OrderDTO> getOrderHistory(Long userId, Pageable pageable);

	    OrderDTO getOrderById(Long orderId);

}
