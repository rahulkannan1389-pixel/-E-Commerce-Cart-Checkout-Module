package com.techpalle.service;

import com.techpalle.dto.request.CheckoutRequest;
import com.techpalle.dto.response.CheckoutResponse;

public interface CheckoutService {
	  CheckoutResponse checkout(CheckoutRequest request);
	  
}
