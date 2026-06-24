package com.techpalle.service;

import java.math.BigDecimal;

import com.techpalle.dto.response.CouponDTO;
import com.techpalle.entity.Coupon;

public interface CouponService {

    BigDecimal calculateDiscount(BigDecimal amount, Coupon coupon);
    CouponDTO validateCoupon(String couponCode);
}
