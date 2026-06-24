package com.techpalle.repository;

import java.io.Serializable;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.techpalle.entity.Cart;

public interface CartRepository extends JpaRepository<Cart, Serializable>{
	Optional<Cart> findByUserId(Long userId);
}
