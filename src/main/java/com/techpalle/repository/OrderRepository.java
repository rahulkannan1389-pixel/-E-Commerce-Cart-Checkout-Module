package com.techpalle.repository;

import java.io.Serializable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import com.techpalle.entity.Order;

public interface OrderRepository extends JpaRepository<Order, Serializable>{

  Page<Order> findByUserId(Long userId, Pageable pageable);
}

