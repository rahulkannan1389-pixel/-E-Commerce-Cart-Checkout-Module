package com.techpalle.serviceimpl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.techpalle.dto.request.AddToCartRequest;
import com.techpalle.dto.request.UpdateCartRequest;
import com.techpalle.dto.response.CartDTO;
import com.techpalle.dto.response.CartItemDTO;
import com.techpalle.entity.Cart;
import com.techpalle.entity.CartItem;
import com.techpalle.entity.Product;
import com.techpalle.entity.User;
import com.techpalle.repository.CartItemRepository;
import com.techpalle.repository.CartRepository;
import com.techpalle.repository.ProductRepository;
import com.techpalle.repository.UserRepository;
import com.techpalle.service.CartService;
import com.techpalle.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class CartServiceImpl implements CartService{

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    @Override
    public CartDTO addToCart(AddToCartRequest request) {

        log.info("Adding product {} to cart for user {}", 
                request.getProductId(), request.getUserId());

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> {
                    log.error("User not found with ID {}", request.getUserId());
                    return new ResourceNotFoundException("User not found");
                });

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> {
                    log.error("Product not found with ID {}", request.getProductId());
                    return new ResourceNotFoundException("Product not found");
                });

        Cart cart = cartRepository.findByUserId(user.getId())
                .orElseGet(() -> {
                    log.info("Creating new cart for user {}", user.getId());
                    return cartRepository.save(
                            Cart.builder().user(user).build()
                    );
                });

        Optional<CartItem> existingItem =
                cartItemRepository.findByCartAndProduct(cart, product);

        if (existingItem.isPresent()) {
            CartItem item = existingItem.get();
            int newQty = item.getQuantity() + request.getQuantity();

            log.debug("Updating quantity for product {} to {}", 
                    product.getId(), newQty);

            item.setQuantity(newQty);
        } else {
            log.info("Adding new item to cart. Product ID: {}", product.getId());

            CartItem newItem = CartItem.builder()
                    .cart(cart)
                    .product(product)
                    .quantity(request.getQuantity())
                    .build();

        CartItem savedItem = cartItemRepository.save(newItem);
        cart.addItem(savedItem);
        }

        log.info("Product successfully added to cart for user {}", user.getId());

        return mapCartToDTO(cart);
    }

    @Override
    public CartDTO updateCart(UpdateCartRequest request) {

        log.info("Updating cart item {}", request.getCartItemId());

        CartItem cartItem = cartItemRepository.findById(request.getCartItemId())
                .orElseThrow(() -> {
                    log.error("Cart item not found: {}", request.getCartItemId());
                    return new ResourceNotFoundException("Cart item not found");
                });

        log.debug("Old Quantity: {}, New Quantity: {}", 
                cartItem.getQuantity(), request.getQuantity());

        cartItem.setQuantity(request.getQuantity());

        log.info("Cart item {} updated successfully", cartItem.getId());

        return mapCartToDTO(cartItem.getCart());
    }

    @Override
    public CartDTO getCart(Long userId) {

        log.info("Fetching cart for user {}", userId);

        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> {
                    log.error("Cart not found for user {}", userId);
                    return new ResourceNotFoundException("Cart not found");
                });

        log.debug("Cart fetched successfully for user {}", userId);

        return mapCartToDTO(cart);
    }

    @Override
    public void removeFromCart(Long cartItemId) {

        log.info("Removing cart item {}", cartItemId);

        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> {
                    log.error("Cart item not found: {}", cartItemId);
                    return new ResourceNotFoundException("Cart item not found");
                });

        cartItemRepository.delete(cartItem);

        log.info("Cart item {} removed successfully", cartItemId);
    }

    @Override
    public void clearCart(Long userId) {

        log.info("Clearing cart for user {}", userId);

        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> {
                    log.error("Cart not found for user {}", userId);
                    return new ResourceNotFoundException("Cart not found");
                });

        cartItemRepository.deleteByCart(cart);
        cart.getCartItems().clear();

        log.info("Cart cleared successfully for user {}", userId);
    }

    private CartDTO mapCartToDTO(Cart cart) {

        log.debug("Mapping cart {} to DTO", cart.getId());

        List<CartItemDTO> items = new ArrayList<>();
        BigDecimal totalPrice = BigDecimal.ZERO;
        int totalItems = 0;

        for (CartItem cartItem : cart.getCartItems()) {

            BigDecimal price = cartItem.getProduct().getPrice();

            BigDecimal subtotal = price.multiply(
                    BigDecimal.valueOf(cartItem.getQuantity())
            );

            items.add(CartItemDTO.builder()
                    .id(cartItem.getId())
                    .productId(cartItem.getProduct().getId())
                    .productName(cartItem.getProduct().getName())
                    .price(price)
                    .quantity(cartItem.getQuantity())
                    .subtotal(subtotal)
                    .build());

            totalPrice = totalPrice.add(subtotal);
            totalItems += cartItem.getQuantity();
        }

        log.debug("Cart mapping complete. Total items: {}, Total price: {}", 
                totalItems, totalPrice);

        return CartDTO.builder()
                .id(cart.getId())
                .userId(cart.getUser().getId())
                .items(items)
                .totalPrice(totalPrice)
                .totalItems(totalItems)
                .build();
    }
}
