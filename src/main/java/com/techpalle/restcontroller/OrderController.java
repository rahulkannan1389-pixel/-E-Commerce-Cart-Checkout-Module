package com.techpalle.restcontroller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.techpalle.dto.response.ApiResponse;
import com.techpalle.dto.response.OrderDTO;
import com.techpalle.service.OrderService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor

public class OrderController {

   private final OrderService orderService;

    //  ORDER HISTORY
    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<Page<OrderDTO>>> getOrderHistory(
            @PathVariable Long userId,
            @PageableDefault(size = 10, page = 0, sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable,
            HttpServletRequest httpRequest) {

        log.info("API call: Fetch order history for user {}", userId);

        Page<OrderDTO> orders = orderService.getOrderHistory(userId, pageable);

        log.debug("Total orders fetched for user {}: {}", userId, orders.getTotalElements());

        return ResponseEntity.ok(
                ApiResponse.success(
                        orders,
                        "Order history fetched successfully",
                        httpRequest.getRequestURI()
                )
        );
    }

    //  GET ORDER BY ID
    @GetMapping("/{orderId}")
    public ResponseEntity<ApiResponse<OrderDTO>> getOrderById(
            @PathVariable Long orderId,
            HttpServletRequest httpRequest) {

        log.info("API call: Fetch order {}", orderId);

        OrderDTO order = orderService.getOrderById(orderId);

        log.debug("Order {} fetched successfully", orderId);

        return ResponseEntity.ok(
                ApiResponse.success(
                        order,
                        "Order fetched successfully",
                        httpRequest.getRequestURI()
                )
        );
    }
}
