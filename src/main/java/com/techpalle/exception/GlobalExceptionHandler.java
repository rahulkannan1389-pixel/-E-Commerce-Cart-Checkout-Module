package com.techpalle.exception;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import com.techpalle.dto.response.ApiResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {

	  // Extract API path
	    private String getPath(WebRequest request) {
	        return request.getDescription(false).replace("uri=", "");
	    }

	    //  Resource Not Found
	    @ExceptionHandler(ResourceNotFoundException.class)
	    public ResponseEntity<ApiResponse<Void>> handleResourceNotFoundException(
	            ResourceNotFoundException ex, WebRequest request) {

	        return new ResponseEntity<>(
	                ApiResponse.error(
	                        HttpStatus.NOT_FOUND.value(),
	                        ex.getMessage(),
	                        List.of("Resource not found"),
	                        getPath(request)
	                ),
	                HttpStatus.NOT_FOUND
	        );
	    }

	    //  Insufficient Stock
	    @ExceptionHandler(InsufficientStockException.class)
	    public ResponseEntity<ApiResponse<Void>> handleInsufficientStockException(
	            InsufficientStockException ex, WebRequest request) {

	        return new ResponseEntity<>(
	                ApiResponse.error(
	                        HttpStatus.BAD_REQUEST.value(),
	                        ex.getMessage(),
	                        List.of("Insufficient stock"),
	                        getPath(request)
	                ),
	                HttpStatus.BAD_REQUEST
	        );
	    }

	    //Invalid Coupon
	    @ExceptionHandler(InvalidCouponException.class)
	    public ResponseEntity<ApiResponse<Void>> handleInvalidCouponException(
	            InvalidCouponException ex, WebRequest request) {

	        return new ResponseEntity<>(
	                ApiResponse.error(
	                        HttpStatus.BAD_REQUEST.value(),
	                        ex.getMessage(),
	                        List.of("Invalid coupon"),
	                        getPath(request)
	                ),
	                HttpStatus.BAD_REQUEST
	        );
	    }

	    // Empty Cart
	    @ExceptionHandler(EmptyCartException.class)
	    public ResponseEntity<ApiResponse<Void>> handleEmptyCartException(
	            EmptyCartException ex, WebRequest request) {

	        return new ResponseEntity<>(
	                ApiResponse.error(
	                        HttpStatus.BAD_REQUEST.value(),
	                        ex.getMessage(),
	                        List.of("Cart is empty"),
	                        getPath(request)
	                ),
	                HttpStatus.BAD_REQUEST
	        );
	    }

	    // DTO Validation
	    @ExceptionHandler(MethodArgumentNotValidException.class)
	    public ResponseEntity<ApiResponse<Void>> handleValidationException(
	            MethodArgumentNotValidException ex, WebRequest request) {

	        List<String> errors = ex.getBindingResult()
	                .getFieldErrors()
	                .stream()
	                .map(error -> error.getField() + ": " + error.getDefaultMessage())
	                .collect(Collectors.toList());

	        return new ResponseEntity<>(
	                ApiResponse.error(
	                        HttpStatus.BAD_REQUEST.value(),
	                        "Validation failed",
	                        errors,
	                        getPath(request)
	                ),
	                HttpStatus.BAD_REQUEST
	        );
	    }

	    //  Generic Exception
	    @ExceptionHandler(Exception.class)
	    public ResponseEntity<ApiResponse<Void>> handleGenericException(
	            Exception ex, WebRequest request) {

	        return new ResponseEntity<>(
	                ApiResponse.error(
	                        HttpStatus.INTERNAL_SERVER_ERROR.value(),
	                        "Something went wrong",
	                        List.of(ex.getClass().getSimpleName()),
	                        getPath(request)
	                ),
	                HttpStatus.INTERNAL_SERVER_ERROR
	        );
	    }
}
