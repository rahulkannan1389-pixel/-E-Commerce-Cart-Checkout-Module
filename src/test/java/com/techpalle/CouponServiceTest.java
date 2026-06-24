package com.techpalle;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.techpalle.dto.response.CouponDTO;
import com.techpalle.entity.Coupon;
import com.techpalle.enums.DiscountType;
import com.techpalle.exception.InvalidCouponException;
import com.techpalle.repository.CouponRepository;
import com.techpalle.serviceimpl.CouponServiceImpl;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CouponService Unit Tests")
public class CouponServiceTest {

	    @Mock
	    private CouponRepository couponRepository;

	    @InjectMocks
	    private CouponServiceImpl couponService;

	    private Coupon validPercentageCoupon;
	    private Coupon validFlatCoupon;
	    private Coupon expiredCoupon;

	    @BeforeEach
	    void setUp() {

	        validPercentageCoupon = Coupon.builder()
	                .code("SAVE10")
	                .discountType(DiscountType.PERCENTAGE)
	                .discountValue(BigDecimal.valueOf(10))
	                .expiryDate(LocalDateTime.now().plusDays(30))
	                .build();

	        validFlatCoupon = Coupon.builder()
	                .code("FLAT50")
	                .discountType(DiscountType.FLAT)
	                .discountValue(BigDecimal.valueOf(50))
	                .expiryDate(LocalDateTime.now().plusDays(30))
	                .build();

	        expiredCoupon = Coupon.builder()
	                .code("EXPIRED")
	                .discountType(DiscountType.PERCENTAGE)
	                .discountValue(BigDecimal.valueOf(20))
	                .expiryDate(LocalDateTime.now().minusDays(1))
	                .build();
	    }

	    @Test
	    @DisplayName("Should validate coupon successfully")
	    void testValidateCoupon_Success() {

	        when(couponRepository.findById("SAVE10"))
	                .thenReturn(Optional.of(validPercentageCoupon));

	        CouponDTO result = couponService.validateCoupon("SAVE10");

	        assertNotNull(result);
	        assertEquals("SAVE10", result.getCode());
	        assertEquals(DiscountType.PERCENTAGE, result.getDiscountType());

	        verify(couponRepository).findById("SAVE10");
	    }

	    @Test
	    @DisplayName("Should throw exception when coupon not found")
	    void testValidateCoupon_NotFound() {

	        when(couponRepository.findById("INVALID"))
	                .thenReturn(Optional.empty());

	        assertThrows(InvalidCouponException.class,
	                () -> couponService.validateCoupon("INVALID"));
	    }

	    @Test
	    @DisplayName("Should throw exception when coupon expired")
	    void testValidateCoupon_Expired() {

	        when(couponRepository.findById("EXPIRED"))
	                .thenReturn(Optional.of(expiredCoupon));

	        assertThrows(InvalidCouponException.class,
	                () -> couponService.validateCoupon("EXPIRED"));
	    }

	    @Test
	    @DisplayName("Should calculate percentage discount")
	    void testCalculateDiscount_Percentage() {

	        BigDecimal amount = BigDecimal.valueOf(1000);

	        BigDecimal discount =
	                couponService.calculateDiscount(amount, validPercentageCoupon);

	        assertEquals(BigDecimal.valueOf(100), discount);
	    }

	    @Test
	    @DisplayName("Should calculate flat discount")
	    void testCalculateDiscount_Flat() {

	        BigDecimal amount = BigDecimal.valueOf(1000);

	        BigDecimal discount =
	                couponService.calculateDiscount(amount, validFlatCoupon);

	        assertEquals(BigDecimal.valueOf(50), discount);
	    }

	    @Test
	    @DisplayName("Should throw exception for invalid discount type")
	    void testCalculateDiscount_InvalidType() {

	        Coupon invalidCoupon = Coupon.builder()
	                .code("INVALID")
	                .discountType(null)
	                .discountValue(BigDecimal.valueOf(10))
	                .expiryDate(LocalDateTime.now().plusDays(30))
	                .build();

	        BigDecimal amount = BigDecimal.valueOf(1000);

	        assertThrows(InvalidCouponException.class,
	                () -> couponService.calculateDiscount(amount, invalidCoupon));
	    }

	    @Test
	    @DisplayName("Should return zero discount for zero amount")
	    void testCalculateDiscount_ZeroAmount() {

	        BigDecimal amount = BigDecimal.ZERO;

	        BigDecimal discount =
	                couponService.calculateDiscount(amount, validPercentageCoupon);

	        assertEquals(BigDecimal.ZERO, discount);
	    }
}
