package com.techpalle;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.techpalle.dto.request.AddToCartRequest;
import com.techpalle.dto.request.UpdateCartRequest;
import com.techpalle.dto.response.CartDTO;
import com.techpalle.entity.Cart;
import com.techpalle.entity.CartItem;
import com.techpalle.entity.Product;
import com.techpalle.entity.User;
import com.techpalle.exception.ResourceNotFoundException;
import com.techpalle.repository.CartItemRepository;
import com.techpalle.repository.CartRepository;
import com.techpalle.repository.ProductRepository;
import com.techpalle.repository.UserRepository;
import com.techpalle.serviceimpl.CartServiceImpl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;



@ExtendWith(MockitoExtension.class)
@DisplayName("CartService Unit Tests")

public class CartServiceTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private CartItemRepository cartItemRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private CartServiceImpl cartService;

    private User user;
    private Product product;
    private Cart cart;
    private CartItem cartItem;

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
    }

    @Test
    @DisplayName("Should add product to cart successfully")
    void testAddToCart_Success() {

        AddToCartRequest request = AddToCartRequest.builder()
                .userId(1L)
                .productId(1L)
                .quantity(2)
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(cart));
        when(cartItemRepository.findByCartAndProduct(cart, product)).thenReturn(Optional.empty());
        when(cartItemRepository.save(any(CartItem.class))) .thenReturn(cartItem);


        CartDTO result = cartService.addToCart(request);

        assertNotNull(result);
        assertEquals(1L, result.getUserId());

        verify(userRepository).findById(1L);
        verify(productRepository).findById(1L);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when user not found")
    void testAddToCart_UserNotFound() {

        AddToCartRequest request = AddToCartRequest.builder()
                .userId(999L)
                .productId(1L)
                .quantity(2)
                .build();

        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> cartService.addToCart(request));
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when product not found")
    void testAddToCart_ProductNotFound() {

        AddToCartRequest request = AddToCartRequest.builder()
                .userId(1L)
                .productId(999L)
                .quantity(2)
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> cartService.addToCart(request));
    }

    @Test
    @DisplayName("Should update cart item successfully")
    void testUpdateCart_Success() {

        UpdateCartRequest request = UpdateCartRequest.builder()
                .cartItemId(1L)
                .quantity(5)
                .build();

        when(cartItemRepository.findById(1L)).thenReturn(Optional.of(cartItem));

        CartDTO result = cartService.updateCart(request);

        assertNotNull(result);
        verify(cartItemRepository).findById(1L);
    }

    @Test
    @DisplayName("Should throw exception when cart item not found")
    void testUpdateCart_CartItemNotFound() {

        UpdateCartRequest request = UpdateCartRequest.builder()
                .cartItemId(999L)
                .quantity(5)
                .build();

        when(cartItemRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> cartService.updateCart(request));
    }

    @Test
    @DisplayName("Should get cart successfully")
    void testGetCart_Success() {

        cart.setCartItems(List.of(cartItem));

        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(cart));

        CartDTO result = cartService.getCart(1L);

        assertNotNull(result);
        assertEquals(1L, result.getUserId());
        assertEquals(1, result.getItems().size());
        assertEquals(BigDecimal.valueOf(2599.98), result.getTotalPrice()); // ✅ FIX
    }

    @Test
    @DisplayName("Should throw exception when cart not found")
    void testGetCart_NotFound() {

        when(cartRepository.findByUserId(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> cartService.getCart(999L));
    }

    @Test
    @DisplayName("Should remove item from cart successfully")
    void testRemoveFromCart_Success() {

        when(cartItemRepository.findById(1L)).thenReturn(Optional.of(cartItem));

        cartService.removeFromCart(1L);

        verify(cartItemRepository).delete(cartItem);
    }

    @Test
    @DisplayName("Should clear cart successfully")
    void testClearCart_Success() {

        cart.setCartItems(new ArrayList<>(List.of(cartItem)));

        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(cart));

        cartService.clearCart(1L);

        verify(cartItemRepository).deleteByCart(cart); // ✅ FIX
    }
}
