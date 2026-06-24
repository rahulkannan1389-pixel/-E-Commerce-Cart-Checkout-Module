package com.techpalle.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import com.techpalle.enums.OrderStatus;
import java.util.ArrayList;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderDTO {

	 private Long id;
	    private Long userId;

	    private BigDecimal totalAmount;

	    private OrderStatus status;

	    private LocalDateTime createdAt;

	    @Builder.Default
	    private List<OrderItemDTO> items = new ArrayList<>();


}
