package com.techpalle.restcontroller;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.techpalle.dto.response.ApiResponse;
import com.techpalle.dto.response.CouponDTO;
import com.techpalle.service.CouponService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/coupons")
@RequiredArgsConstructor
public class CouponController {
  
   private final CouponService couponService;

    //  VALIDATE COUPON
    @GetMapping("/{code}")
    public ResponseEntity<ApiResponse<CouponDTO>> validateCoupon(
            @PathVariable String code,
            HttpServletRequest httpRequest) {

        log.info("API call: Validate coupon {}", code);

        CouponDTO coupon = couponService.validateCoupon(code);

        log.debug("Coupon {} is valid", code);

        return ResponseEntity.ok(
                ApiResponse.success(
                        coupon,
                        "Coupon is valid",
                        httpRequest.getRequestURI()
                )
        );
    }
}

