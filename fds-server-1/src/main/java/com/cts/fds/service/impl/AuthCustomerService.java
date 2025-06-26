package com.cts.fds.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.cts.fds.dto.CustomerCreateDTO;
import com.cts.fds.dto.CustomerLoginDTO;
import com.cts.fds.dto.CustomerResponseDTO;
import com.cts.fds.dto.LoginResponseDTO;
import com.cts.fds.entity.Cart;
import com.cts.fds.entity.Customer;
import com.cts.fds.exception.ResourceNotFoundException;
import com.cts.fds.repository.CartRepository;
import com.cts.fds.repository.CustomerRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class AuthCustomerService {

	@Autowired
	CustomerRepository userRepository;
	@Autowired
	JwtCustomerService jwtCustomerService;
	@Autowired
	PasswordEncoder passwordEncoder;

	@Autowired
	CartRepository cartRepository;

	@Autowired
	AuthenticationManager authenticationManager;

	public ResponseEntity<CustomerResponseDTO> register(CustomerCreateDTO request) {

		Customer customer = new Customer();
		customer.setUsername(request.getUsername());
		customer.setPassword(passwordEncoder.encode(request.getPassword()));
		customer.setAddress(request.getAddress());
		customer.setPhone(request.getPhone());
		customer.setEmail(request.getEmail());

		try {
			userRepository.save(customer);
			Cart cart = new Cart();
			cart.setCustomer(customer);
			customer.setCart(cart);
			cartRepository.save(cart); // Save the cart
		} catch (Exception e) {
			log.error("Oops! Already a user");
			return ResponseEntity.badRequest()
					.body(new CustomerResponseDTO(customer.getId(), customer.getUsername(), customer.getAddress(),
							customer.getPhone(), customer.getEmail(), "Username already exists", "null-token"));
		}
		String token = jwtCustomerService.generateToken(customer);
		log.info("User Registration Phase Run Successful");
		return ResponseEntity
				.ok(new CustomerResponseDTO(customer.getId(), customer.getUsername(), customer.getAddress(),
						customer.getPhone(), customer.getEmail(), "User registered successfully as Customer", token));
	}

	public ResponseEntity<LoginResponseDTO> login(CustomerLoginDTO request) {

		try {
			authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
			Customer customer = userRepository.findByUsername(request.getUsername())
					.orElseThrow(() -> new ResourceNotFoundException("Invalid username or password"));
			String token = jwtCustomerService.generateToken(customer);
			log.info("User Login Phase Run Successful");
			return ResponseEntity.ok(new LoginResponseDTO("Login successful as User", customer.getId(), token));

		} catch (Exception e) {
			log.error("Oops! Invalid Credentials" + e);
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
					.body(new LoginResponseDTO("Invalid email or password", null, "null-token"));

		}

	}
}
