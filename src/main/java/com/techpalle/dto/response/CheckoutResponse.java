package com.techpalle.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import com.techpalle.enums.OrderStatus;
import com.techpalle.enums.PaymentStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CheckoutResponse {

private Long orderId;

    private Long userId;

    private BigDecimal subtotal;

    private BigDecimal discountAmount;

    private BigDecimal totalAmount;

    private PaymentStatus paymentStatus;

    private OrderStatus orderStatus;

    private LocalDateTime createdAt;

    private String message;

}
