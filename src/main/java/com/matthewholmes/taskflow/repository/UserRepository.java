package com.matthewholmes.taskflow.repository;

import org.springframework.stereotype.Repository;
import com.matthewholmes.taskflow.model.User;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
	
	Optional<User> findByEmail(String email);
	
	boolean existsByEmail(String email);
}
