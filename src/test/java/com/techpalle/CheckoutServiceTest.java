package com.techpalle;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.techpalle.dto.request.CheckoutRequest;
import com.techpalle.dto.response.CheckoutResponse;
import com.techpalle.dto.response.CouponDTO;
import com.techpalle.entity.Cart;
import com.techpalle.entity.CartItem;
import com.techpalle.entity.Coupon;
import com.techpalle.entity.Order;
import com.techpalle.entity.Product;
import com.techpalle.entity.User;
import com.techpalle.enums.DiscountType;
import com.techpalle.enums.OrderStatus;
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
import com.techpalle.service.CouponService;
import com.techpalle.serviceimpl.CheckoutServiceImpl;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CheckoutService Unit Tests")
public class CheckoutServiceTest {


    @Mock
    private CartRepository cartRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderItemRepository orderItemRepository;

    @Mock
    private CartItemRepository cartItemRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CouponService couponService;

    @InjectMocks
    private CheckoutServiceImpl checkoutService;  

    @Mock
    private CouponRepository couponRepository;


    private User user;
    private Product product;
    private Cart cart;
    private CartItem cartItem;
    private Order order;
    private Coupon coupon;

    @BeforeEach
    void setUp() {

        user = User.builder()
                .id(1L)
                .name("John Doe")
                .email("john@example.com")
                .build();

        product = Product.builder()
                .id(1L)
                .name("Laptop")
                .price(BigDecimal.valueOf(1299.99))
                .stockQuantity(50)
                .build();

        cart = Cart.builder()
                .id(1L)
                .user(user)
                .cartItems(new ArrayList<>())
                .build();

        cartItem = CartItem.builder()
                .id(1L)
                .cart(cart)
                .product(product)
                .quantity(2)
                .build();

        order = Order.builder()
                .id(1L)
                .user(user)
                .totalAmount(BigDecimal.valueOf(2599.98))
                .status(OrderStatus.SUCCESS)
                .createdAt(LocalDateTime.now())
                .build();

        coupon = Coupon.builder()
                .code("SAVE10")
                .discountType(DiscountType.PERCENTAGE)
                .discountValue(BigDecimal.valueOf(10))
                .expiryDate(LocalDateTime.now().plusDays(10))
                .build();
    }

    @Test
    void testCheckout_SuccessWithoutCoupon() {

        cart.setCartItems(new ArrayList<>());
        cart.getCartItems().add(cartItem);

        CheckoutRequest request = CheckoutRequest.builder()
                .userId(1L)
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(cart));
        when(orderRepository.save(any(Order.class))).thenReturn(order);
        when(productRepository.save(any(Product.class))).thenReturn(product);

        CheckoutResponse response = checkoutService.checkout(request);

        assertNotNull(response);
        assertEquals(1L, response.getOrderId());
    }

    @Test
    void testCheckout_SuccessWithCoupon() {

        cart.setCartItems(new ArrayList<>());
        cart.getCartItems().add(cartItem);

        CheckoutRequest request = CheckoutRequest.builder()
                .userId(1L)
                .couponCode("SAVE10")
                .build();

        when(couponRepository.findById("SAVE10"))
           .thenReturn(Optional.of(coupon));


        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(cart));

        // IMPORTANT FIX
        CouponDTO couponDTO = CouponDTO.builder()
                .code("SAVE10")
                .discountType(DiscountType.PERCENTAGE)
                .discountValue(BigDecimal.valueOf(10))
                .build();

        when(couponService.validateCoupon("SAVE10")).thenReturn(couponDTO);
        when(couponService.calculateDiscount(any(BigDecimal.class), any()))
                .thenReturn(BigDecimal.valueOf(129.99));

        when(orderRepository.save(any(Order.class))).thenReturn(order);
        when(productRepository.save(any(Product.class))).thenReturn(product);

        CheckoutResponse response = checkoutService.checkout(request);

        assertNotNull(response);
        assertEquals(1L, response.getOrderId());
    }

    @Test
    void testCheckout_UserNotFound() {

        CheckoutRequest request = CheckoutRequest.builder()
                .userId(99L)
                .build();

        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> checkoutService.checkout(request));
    }

    @Test
    void testCheckout_EmptyCart() {

        CheckoutRequest request = CheckoutRequest.builder()
                .userId(1L)
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(cart));

        assertThrows(EmptyCartException.class,
                () -> checkoutService.checkout(request));
    }

    @Test
    void testCheckout_InsufficientStock() {

        cartItem.setQuantity(100);

        cart.setCartItems(new ArrayList<>());
        cart.getCartItems().add(cartItem);

        CheckoutRequest request = CheckoutRequest.builder()
                .userId(1L)
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(cart));

        assertThrows(InsufficientStockException.class,
                () -> checkoutService.checkout(request));
    }

    @Test
    void testCheckout_CartCleared() {

        cart.setCartItems(new ArrayList<>());
        cart.getCartItems().add(cartItem);

        CheckoutRequest request = CheckoutRequest.builder()
                .userId(1L)
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(cart));
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        checkoutService.checkout(request);

        verify(cartItemRepository).deleteByCart(cart);
    }
}
