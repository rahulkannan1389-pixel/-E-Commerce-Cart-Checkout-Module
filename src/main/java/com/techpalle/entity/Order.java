package com.techpalle.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.techpalle.enums.OrderStatus;

@Entity
@Table(name = "orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {


	    @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Long id;

	    @ManyToOne(fetch = FetchType.LAZY)
	    @JoinColumn(name = "user_id", nullable = false)
	    private User user;

	    @NotNull(message = "Total amount is required")
	    @Column(nullable = false)
	    private BigDecimal totalAmount;

	    @Enumerated(EnumType.STRING)
	    @Column(nullable = false)
	    private OrderStatus status;

	    @Column(nullable = false)
	    private LocalDateTime createdAt;

	    @OneToMany(mappedBy = "order",
	            cascade = CascadeType.ALL,
	            orphanRemoval = true)

        @Builder.Default
	    private List<OrderItem> orderItems = new ArrayList<>();

	    @PrePersist
	    public void prePersist() {
	        this.createdAt = LocalDateTime.now();
	    }
}
