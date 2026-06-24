package com.techpalle.dto.response;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItemDTO {

	    private Long id;
	    private Long productId;
	    private String productName;

	    private Integer quantity;

	    private BigDecimal price;

}
