package com.techpalle.config;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import com.techpalle.entity.Coupon;
import com.techpalle.entity.Product;
import com.techpalle.entity.User;
import com.techpalle.enums.DiscountType;
import com.techpalle.repository.CouponRepository;
import com.techpalle.repository.ProductRepository;
import com.techpalle.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final CouponRepository couponRepository;

    @Override
    public void run(String... args) throws Exception {
        
        // Load sample products if they don't exist
        if (productRepository.count() == 0) {
           
            
            productRepository.save(Product.builder()
                    .name("Laptop")
                    .price(BigDecimal.valueOf(999.99))
                    .stockQuantity(50)
                    .build());
            
            productRepository.save(Product.builder()
                    .name("Mouse")
                    .price(BigDecimal.valueOf(29.99))
                    .stockQuantity(200)
                    .build());
            
            productRepository.save(Product.builder()
                    .name("Keyboard")
                    .price(BigDecimal.valueOf(79.99))
                    .stockQuantity(150)
                    .build());
            
            productRepository.save(Product.builder()
                    .name("Monitor")
                    .price(BigDecimal.valueOf(299.99))
                    .stockQuantity(80)
                    .build());
            
            productRepository.save(Product.builder()
                    .name("Headphones")
                    .price(BigDecimal.valueOf(149.99))
                    .stockQuantity(120)
                    .build());
                     
        }
        
        // Load sample users if they don't exist
        if (userRepository.count() == 0) {
       
            
            userRepository.save(User.builder()
                    .name("John Doe")
                    .email("john@example.com")
                    .build());
            
            userRepository.save(User.builder()
                    .name("Jane Smith")
                    .email("jane@example.com")
                    .build());
            
            userRepository.save(User.builder()
                    .name("Bob Johnson")
                    .email("bob@example.com")
                    .build());
                  
        }
        
        // Load sample coupons if they don't exist
        if (couponRepository.count() == 0) {
          
            
            couponRepository.save(Coupon.builder()
                    .code("SAVE100")
                    .discountType(DiscountType.FLAT)
                    .discountValue(BigDecimal.valueOf(100.00))
                    .expiryDate(LocalDateTime.now().plusMonths(6))
                    .build());
            
            couponRepository.save(Coupon.builder()
                    .code("SAVE20")
                    .discountType(DiscountType.PERCENTAGE)
                    .discountValue(BigDecimal.valueOf(20))
                    .expiryDate(LocalDateTime.now().plusMonths(6))
                    .build());
            
            couponRepository.save(Coupon.builder()
                    .code("SUMMER50")
                    .discountType(DiscountType.FLAT)
                    .discountValue(BigDecimal.valueOf(50.00))
                    .expiryDate(LocalDateTime.now().plusMonths(3))
                    .build());
            
            couponRepository.save(Coupon.builder()
                    .code("DISCOUNT15")
                    .discountType(DiscountType.PERCENTAGE)
                    .discountValue(BigDecimal.valueOf(15))
                    .expiryDate(LocalDateTime.now().plusMonths(6))
                    .build());  
        }
        
    }
}
