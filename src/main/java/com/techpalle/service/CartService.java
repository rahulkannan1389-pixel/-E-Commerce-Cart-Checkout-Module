package com.techpalle.service;

import com.techpalle.dto.request.AddToCartRequest;
import com.techpalle.dto.request.UpdateCartRequest;
import com.techpalle.dto.response.CartDTO;

public interface CartService {

	    CartDTO addToCart(AddToCartRequest request);

	    CartDTO updateCart(UpdateCartRequest request);

	    CartDTO getCart(Long userId);

	    void removeFromCart(Long cartItemId);

	    void clearCart(Long userId);
	}

