package com.techpalle.dto.response;

import java.math.BigDecimal;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.ArrayList;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartDTO {

	    private Long id;
	    private Long userId;

	    @Builder.Default
	    private List<CartItemDTO> items = new ArrayList<>();

	    private BigDecimal totalPrice;

	    private Integer totalItems;

}
