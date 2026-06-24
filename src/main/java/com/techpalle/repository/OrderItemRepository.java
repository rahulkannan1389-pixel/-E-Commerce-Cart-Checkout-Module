package com.techpalle.repository;

import java.io.Serializable;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.techpalle.entity.Order;
import com.techpalle.entity.OrderItem;

public interface OrderItemRepository extends JpaRepository<OrderItem, Serializable>{
	 List<OrderItem> findByOrder(Order order);

}
