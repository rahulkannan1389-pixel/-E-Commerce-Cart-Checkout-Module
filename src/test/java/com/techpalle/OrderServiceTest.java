package com.techpalle;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import com.techpalle.dto.response.OrderDTO;
import com.techpalle.entity.Order;
import com.techpalle.entity.OrderItem;
import com.techpalle.entity.Product;
import com.techpalle.entity.User;
import com.techpalle.enums.OrderStatus;
import com.techpalle.exception.ResourceNotFoundException;
import com.techpalle.repository.OrderRepository;
import com.techpalle.serviceimpl.OrderServiceImpl;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("OrderService Unit Tests")
public class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderServiceImpl orderService;

    private User user;
    private Product product;
    private Order order;
    private OrderItem orderItem;

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

        orderItem = OrderItem.builder()
                .id(1L)
                .product(product)
                .quantity(2)
                .price(BigDecimal.valueOf(1299.99))
                .build();

        order = Order.builder()
                .id(1L)
                .user(user)
                .totalAmount(BigDecimal.valueOf(2599.98))
                .status(OrderStatus.SUCCESS)
                .createdAt(LocalDateTime.now())
                .orderItems(new ArrayList<>(List.of(orderItem)))
                .build();

        orderItem.setOrder(order);
    }

    @Test
    @DisplayName("Should get order history successfully")
    void testGetOrderHistory_Success() {

        Pageable pageable = PageRequest.of(0, 10);
        Page<Order> orderPage = new PageImpl<>(List.of(order), pageable, 1);

        when(orderRepository.findByUserId(1L, pageable)).thenReturn(orderPage);

        Page<OrderDTO> result = orderService.getOrderHistory(1L, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());

        OrderDTO dto = result.getContent().get(0);
        assertEquals(1L, dto.getId());
        assertEquals(OrderStatus.SUCCESS, dto.getStatus());

        verify(orderRepository).findByUserId(1L, pageable);
    }

    @Test
    @DisplayName("Should return empty order list")
    void testGetOrderHistory_Empty() {

        Pageable pageable = PageRequest.of(0, 10);
        Page<Order> emptyPage = new PageImpl<>(List.of(), pageable, 0);

        when(orderRepository.findByUserId(1L, pageable)).thenReturn(emptyPage);

        Page<OrderDTO> result = orderService.getOrderHistory(1L, pageable);

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Should get order by ID")
    void testGetOrderById_Success() {

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        OrderDTO result = orderService.getOrderById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(BigDecimal.valueOf(2599.98), result.getTotalAmount());
        assertEquals(OrderStatus.SUCCESS, result.getStatus());

        verify(orderRepository).findById(1L);
    }

    @Test
    @DisplayName("Should throw exception when order not found")
    void testGetOrderById_NotFound() {

        when(orderRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> orderService.getOrderById(99L));
    }

    @Test
    @DisplayName("Should map order items correctly")
    void testOrderItemMapping() {

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        OrderDTO result = orderService.getOrderById(1L);

        assertEquals(1, result.getItems().size());
        assertEquals("Laptop", result.getItems().get(0).getProductName());
        assertEquals(2, result.getItems().get(0).getQuantity());
    }
}
