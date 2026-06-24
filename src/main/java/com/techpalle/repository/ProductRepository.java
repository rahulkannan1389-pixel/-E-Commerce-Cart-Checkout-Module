package com.techpalle.repository;

import java.io.Serializable;

import org.springframework.data.jpa.repository.JpaRepository;

import com.techpalle.entity.Product;

public interface ProductRepository extends JpaRepository<Product, Serializable>{
	

}
