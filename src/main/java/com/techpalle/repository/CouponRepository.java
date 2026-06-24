package com.techpalle.repository;

import java.io.Serializable;
import org.springframework.data.jpa.repository.JpaRepository;
import com.techpalle.entity.Coupon;

public interface CouponRepository extends JpaRepository<Coupon, Serializable>{
  	
 
}
