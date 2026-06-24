package com.techpalle.restcontroller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.techpalle.dto.request.AddToCartRequest;
import com.techpalle.dto.request.UpdateCartRequest;
import com.techpalle.dto.response.ApiResponse;
import com.techpalle.dto.response.CartDTO;
import com.techpalle.service.CartService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;
    
    // ADD TO CART
    @PostMapping
    public ResponseEntity<ApiResponse<CartDTO>> addToCart(
            @Valid @RequestBody AddToCartRequest request,
            HttpServletRequest httpRequest) {

        log.info("API call: Add to cart for user {}", request.getUserId());

        CartDTO cart = cartService.addToCart(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.success(cart, "Item added to cart", httpRequest.getRequestURI())
        );
    }

    // UPDATE CART
    @PutMapping
    public ResponseEntity<ApiResponse<CartDTO>> updateCart(
            @Valid @RequestBody UpdateCartRequest request,
            HttpServletRequest httpRequest) {

        log.info("API call: Update cart item {}", request.getCartItemId());

        CartDTO cart = cartService.updateCart(request);

        return ResponseEntity.ok(
                ApiResponse.success(cart, "Cart updated successfully", httpRequest.getRequestURI())
        );
    }

    // GET CART
    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<CartDTO>> getCart(
            @PathVariable Long userId,
            HttpServletRequest httpRequest) {

        log.info("API call: Get cart for user {}", userId);

        CartDTO cart = cartService.getCart(userId);

        return ResponseEntity.ok(
                ApiResponse.success(cart, "Cart fetched successfully", httpRequest.getRequestURI())
        );
    }

    //  REMOVE ITEM
    @DeleteMapping("/item/{cartItemId}")
    public ResponseEntity<ApiResponse<Void>> removeFromCart(
            @PathVariable Long cartItemId,
            HttpServletRequest httpRequest) {

        log.info("API call: Remove cart item {}", cartItemId);

        cartService.removeFromCart(cartItemId);

        return ResponseEntity.ok(
                ApiResponse.success(null, "Item removed from cart", httpRequest.getRequestURI())
        );
    }

    // CLEAR CART
    @DeleteMapping("/clear/{userId}")
    public ResponseEntity<ApiResponse<Void>> clearCart(
            @PathVariable Long userId,
            HttpServletRequest httpRequest) {

        log.info("API call: Clear cart for user {}", userId);

        cartService.clearCart(userId);

        return ResponseEntity.ok(
                ApiResponse.success(null, "Cart cleared successfully", httpRequest.getRequestURI())
        );
    }
}
