package com.techpalle.serviceimpl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.techpalle.dto.response.CouponDTO;
import com.techpalle.entity.Coupon;
import com.techpalle.enums.DiscountType;
import com.techpalle.exception.InvalidCouponException;
import com.techpalle.repository.CouponRepository;
import com.techpalle.service.CouponService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@Service
@RequiredArgsConstructor
public class CouponServiceImpl implements CouponService {
	
   @Autowired
   private  CouponRepository couponRepository;

    @Override
    public CouponDTO validateCoupon(String couponCode) {

        log.info("Validating coupon: {}", couponCode);

        Coupon coupon = couponRepository.findById(couponCode)
                .orElseThrow(() -> {
                    log.error("Coupon not found: {}", couponCode);
                    return new InvalidCouponException("Coupon not found");
                });

        if (coupon.getExpiryDate().isBefore(LocalDateTime.now())) {
            log.warn("Coupon expired: {}", couponCode);
            throw new InvalidCouponException("Coupon expired");
        }

        log.info("Coupon is valid: {}", couponCode);

        return CouponDTO.builder()
                .code(coupon.getCode())
                .discountType(coupon.getDiscountType())
                .discountValue(coupon.getDiscountValue())
                .expiryDate(coupon.getExpiryDate())
                .build();
    }

    @Override
    public BigDecimal calculateDiscount(BigDecimal amount, Coupon coupon) {

        log.info("Calculating discount for coupon: {}", coupon.getCode());

        if (coupon.getDiscountType() == DiscountType.PERCENTAGE) {
            return amount.multiply(coupon.getDiscountValue())
                    .divide(BigDecimal.valueOf(100));
        }

        if (coupon.getDiscountType() == DiscountType.FLAT) {
            return coupon.getDiscountValue();
        }

        log.error("Invalid discount type: {}", coupon.getDiscountType());
        throw new InvalidCouponException("Invalid discount type");
    }
}
