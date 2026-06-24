package com.techpalle.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CheckoutRequest {

	   @NotNull(message = "User ID cannot be null")
	    private Long userId;
	    
	    @Size(max = 50, message = "Coupon code too long")
	    private String couponCode;
}
