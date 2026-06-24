package com.techpalle.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "carts")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cart {

	    @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Long id;

	    @OneToOne(fetch = FetchType.LAZY)
	    @JoinColumn(name = "user_id", nullable = false, unique = true)
	    private User user;

	    @OneToMany(mappedBy = "cart",
	            cascade = CascadeType.ALL,
	            orphanRemoval = true)

        @Builder.Default
        private List<CartItem> cartItems = new ArrayList<>();

	    public void addItem(CartItem item) {
	        cartItems.add(item);
	        item.setCart(this);
	    }

	    public void removeItem(CartItem item) {
	        cartItems.remove(item);
	        item.setCart(null);
	    }
}
