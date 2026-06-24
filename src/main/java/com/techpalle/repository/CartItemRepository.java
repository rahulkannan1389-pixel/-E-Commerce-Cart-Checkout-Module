package com.techpalle.repository;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.techpalle.entity.Cart;
import com.techpalle.entity.CartItem;
import com.techpalle.entity.Product;

public interface CartItemRepository extends JpaRepository<CartItem, Serializable> {

	   Optional<CartItem> findByCartAndProduct(Cart cart, Product product);

	    List<CartItem> findByCart(Cart cart);

	    void deleteByCartAndProduct(Cart cart, Product product);

	    void deleteByCart(Cart cart);

}
