package com.techpalle.repository;

import java.io.Serializable;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import com.techpalle.entity.User;

public interface UserRepository extends JpaRepository<User, Serializable>{
	
	 Optional<User> findByEmail(String email);
}
