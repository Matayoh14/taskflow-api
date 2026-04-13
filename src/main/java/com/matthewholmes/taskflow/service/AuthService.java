package com.matthewholmes.taskflow.service;


import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.matthewholmes.taskflow.dto.AuthResponse;
import com.matthewholmes.taskflow.dto.LoginRequest;
import com.matthewholmes.taskflow.dto.RegisterRequest;
import com.matthewholmes.taskflow.model.User;
import com.matthewholmes.taskflow.repository.UserRepository;
import com.matthewholmes.taskflow.security.JwtService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final AuthenticationManager authenticationManager;
	private final JwtService jwtService;
	
	public AuthResponse register(RegisterRequest request) {
		// Check in email already exists
		if(userRepository.existsByEmail(request.getEmail())) {
			throw new RuntimeException("Email already registered");
		}
		
		// Create new user
		User user = new User();
		user.setEmail(request.getEmail());
		user.setFullName(request.getFullName());
		user.setPassword(passwordEncoder.encode(request.getPassword()));
		user.setRole(User.Role.MEMBER);
		
		userRepository.save(user);
		
		// Generate token
		String token = jwtService.generateToken(user);
		
		return new AuthResponse(token, user.getEmail(), user.getFullName(), user.getRole().name()); 
	}
	
	public AuthResponse login(LoginRequest request) {
		// Spring Security Authentication
		authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(
						request.getEmail(),
						request.getPassword()
				)
		);
		
		// If authentication succeeds, get user details
		User user = userRepository.findByEmail(request.getEmail())
				.orElseThrow(() -> new RuntimeException("User not found"));
		
		// Generate token
		String token = jwtService.generateToken(user);
		
		return new AuthResponse(token, user.getEmail(), user.getFullName(), user.getRole().name());
	}
	
	
}
