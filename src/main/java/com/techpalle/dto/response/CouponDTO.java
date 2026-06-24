package com.techpalle.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import com.techpalle.enums.DiscountType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CouponDTO {

    private String code;

    private DiscountType discountType;

    private BigDecimal discountValue;

    private LocalDateTime expiryDate;

}
