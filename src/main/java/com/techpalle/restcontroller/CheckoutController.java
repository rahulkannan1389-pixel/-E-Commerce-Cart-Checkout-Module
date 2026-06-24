package com.techpalle.restcontroller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.techpalle.dto.request.CheckoutRequest;
import com.techpalle.dto.response.ApiResponse;
import com.techpalle.dto.response.CheckoutResponse;
import com.techpalle.enums.PaymentStatus;
import com.techpalle.service.CheckoutService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/checkout")
@RequiredArgsConstructor
public class CheckoutController {

	    private final CheckoutService checkoutService;

	    @PostMapping
	    public ResponseEntity<ApiResponse<CheckoutResponse>> checkout(
	            @Valid @RequestBody CheckoutRequest request,
	            HttpServletRequest httpRequest) {

	        log.info("API call: Checkout for user {}", request.getUserId());

	        CheckoutResponse response = checkoutService.checkout(request);

	        //  Decide status based on PAYMENT 
	        HttpStatus status = (response.getPaymentStatus() == PaymentStatus.SUCCESS)
	                ? HttpStatus.CREATED
	                : HttpStatus.OK;

	        String message = (response.getPaymentStatus() == PaymentStatus.SUCCESS)
	                ? "Order placed successfully"
	                : "Payment failed. Please try again";

	        log.info("Checkout completed for user {} with status {}", 
	                request.getUserId(), response.getPaymentStatus());

	        return ResponseEntity.status(status).body(
	                ApiResponse.success(response, message, httpRequest.getRequestURI())
	        );
	    }
}
