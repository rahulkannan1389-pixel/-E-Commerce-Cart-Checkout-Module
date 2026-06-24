package com.techpalle.serviceimpl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.techpalle.dto.request.CheckoutRequest;
import com.techpalle.dto.response.CheckoutResponse;
import com.techpalle.entity.Cart;
import com.techpalle.entity.CartItem;
import com.techpalle.entity.Coupon;
import com.techpalle.entity.Order;
import com.techpalle.entity.OrderItem;
import com.techpalle.entity.Product;
import com.techpalle.entity.User;
import com.techpalle.enums.OrderStatus;
import com.techpalle.enums.PaymentStatus;
import com.techpalle.exception.EmptyCartException;
import com.techpalle.exception.InsufficientStockException;
import com.techpalle.exception.ResourceNotFoundException;
import com.techpalle.repository.CartItemRepository;
import com.techpalle.repository.CartRepository;
import com.techpalle.repository.CouponRepository;
import com.techpalle.repository.OrderItemRepository;
import com.techpalle.repository.OrderRepository;
import com.techpalle.repository.ProductRepository;
import com.techpalle.repository.UserRepository;
import com.techpalle.service.CheckoutService;
import com.techpalle.service.CouponService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class CheckoutServiceImpl implements CheckoutService{

    private final CartRepository cartRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final CouponRepository couponRepository;
    private final CouponService couponService;

    @Override
    public CheckoutResponse checkout(CheckoutRequest request) {

        log.info("Starting checkout for user {}", request.getUserId());

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> {
                    log.error("User not found: {}", request.getUserId());
                    return new ResourceNotFoundException("User not found");
                });

        Cart cart = cartRepository.findByUserId(user.getId())
                .orElseThrow(() -> {
                    log.error("Cart not found for user {}", user.getId());
                    return new ResourceNotFoundException("Cart not found");
                });

        if (cart.getCartItems().isEmpty()) {
            log.warn("Empty cart for user {}", user.getId());
            throw new EmptyCartException("Cart is empty");
        }

        //  STOCK VALIDATION
        for (CartItem item : cart.getCartItems()) {
            if (item.getProduct().getStockQuantity() < item.getQuantity()) {
                log.error("Insufficient stock for product {}", item.getProduct().getId());
                throw new InsufficientStockException("Insufficient stock");
            }
        }

        //  CALCULATE TOTAL
        BigDecimal subtotal = calculateCartTotal(cart);
        BigDecimal discountAmount = BigDecimal.ZERO;

        //  APPLY COUPON
        if (request.getCouponCode() != null && !request.getCouponCode().isBlank()) {

            log.info("Applying coupon {}", request.getCouponCode());

            Coupon coupon = couponRepository.findById(request.getCouponCode())
                    .orElseThrow(() -> new ResourceNotFoundException("Coupon not found"));

            couponService.validateCoupon(request.getCouponCode());

            discountAmount = couponService.calculateDiscount(subtotal, coupon);
        }

        BigDecimal totalAmount = subtotal.subtract(discountAmount);

        // PAYMENT SIMULATION
        PaymentStatus paymentStatus = simulatePayment();

        Order order = Order.builder()
                .user(user)
                .totalAmount(totalAmount)
                .status(paymentStatus == PaymentStatus.SUCCESS ?
                        OrderStatus.SUCCESS : OrderStatus.FAILED)
                .createdAt(LocalDateTime.now())
                .build();

        Order savedOrder = orderRepository.save(order);

        if (paymentStatus == PaymentStatus.SUCCESS) {

            log.info("Payment SUCCESS for user {}", user.getId());

            List<OrderItem> orderItems = new ArrayList<>();

            for (CartItem cartItem : cart.getCartItems()) {

                Product product = cartItem.getProduct();

                //  reduce inventory
                product.setStockQuantity(
                        product.getStockQuantity() - cartItem.getQuantity()
                );

                productRepository.save(product);

                OrderItem orderItem = OrderItem.builder()
                        .order(savedOrder)
                        .product(product)
                        .quantity(cartItem.getQuantity())
                        .price(product.getPrice())
                        .build();

                orderItems.add(orderItem);
            }

            orderItemRepository.saveAll(orderItems);

            //  CLEAR CART
            cartItemRepository.deleteByCart(cart);
            cart.getCartItems().clear();

        } else {
            log.warn("Payment FAILED for user {}", user.getId());
        }

        return CheckoutResponse.builder()
                .orderId(savedOrder.getId())
                .userId(user.getId())
                .subtotal(subtotal)
                .discountAmount(discountAmount)
                .totalAmount(totalAmount)
                .paymentStatus(paymentStatus)
                .orderStatus(savedOrder.getStatus())
                .createdAt(savedOrder.getCreatedAt())
                .message(paymentStatus == PaymentStatus.SUCCESS
                        ? "Order placed successfully"
                        : "Payment failed")
                .build();
    }

    private BigDecimal calculateCartTotal(Cart cart) {

        return cart.getCartItems().stream()
                .map(item ->
                        item.getProduct().getPrice()
                                .multiply(BigDecimal.valueOf(item.getQuantity()))
                )
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private PaymentStatus simulatePayment() {

        boolean success = Math.random() < 0.9;

        return success ? PaymentStatus.SUCCESS : PaymentStatus.FAILED;
    }
}

