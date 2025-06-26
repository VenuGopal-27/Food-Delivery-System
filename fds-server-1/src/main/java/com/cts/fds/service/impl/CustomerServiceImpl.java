package com.cts.fds.service.impl;

import com.cts.fds.dto.LoginResponseDTO;
import com.cts.fds.dto.CustomerCreateDTO;
import com.cts.fds.dto.CustomerLoginDTO;
import com.cts.fds.dto.CustomerResponseDTO;
import com.cts.fds.dto.FoodItemResponseDTO;
import com.cts.fds.dto.OrderResponseDTO;
import com.cts.fds.dto.RestaurantResponseDTO;
import com.cts.fds.entity.Cart;
import com.cts.fds.entity.Customer;
import com.cts.fds.entity.FoodItem;
import com.cts.fds.entity.Order;
import com.cts.fds.entity.Restaurant;
import com.cts.fds.exception.ResourceNotFoundException;
import com.cts.fds.repository.CartRepository;
import com.cts.fds.repository.CustomerRepository;
import com.cts.fds.repository.FoodItemRepository;
import com.cts.fds.repository.OrderRepository;
import com.cts.fds.repository.RestaurantRepository;
import com.cts.fds.service.CustomerService;

import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CustomerServiceImpl implements CustomerService {
	
    private static final Logger logger = LoggerFactory.getLogger(CustomerServiceImpl.class);

    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private FoodItemRepository foodItemRepository;
    @Autowired
    private RestaurantRepository restaurantRepository;
    @Autowired
    private OrderRepository orderRepository;


//    @Override
//    @Transactional
//    public ResponseEntity<CustomerResponseDTO> registerCustomer(CustomerCreateDTO customerCreateDTO) {
//        logger.info("Attempting to register new customer with username: {}", customerCreateDTO.getUsername());
//        if (customerRepository.findByUsername(customerCreateDTO.getUsername()).isPresent()) {
//            throw new RuntimeException("Username already exists.");
//        }
//        if (customerRepository.findByEmail(customerCreateDTO.getEmail()).isPresent()) {
//            throw new RuntimeException("Email already registered.");
//        }
//
//        Customer customer = new Customer();
//        customer.setUsername(customerCreateDTO.getUsername());
//        customer.setPassword(customerCreateDTO.getPassword()); // Storing plain text password
//        customer.setAddress(customerCreateDTO.getAddress());
//        customer.setPhone(customerCreateDTO.getPhone());
//        customer.setEmail(customerCreateDTO.getEmail());
//
//        customer = customerRepository.save(customer);
//
//        // Create a new cart for the customer upon registration
//        Cart cart = new Cart();
//        cart.setCustomer(customer);
//        customer.setCart(cart); // Link cart to customer
//        cartRepository.save(cart); // Save the cart
//
//        logger.info("Customer '{}' registered successfully with ID: {}", customer.getUsername(), customer.getId());
//        return ResponseEntity.ok(mapCustomerToResponseDTO(customer));
//    }
//
//    @Override
//    public ResponseEntity<LoginResponseDTO> loginCustomer(CustomerLoginDTO customerLoginDTO) {
//    	Customer customer = customerRepository.findByUsername(customerLoginDTO.getUsername())
//                .orElseThrow(() -> new ResourceNotFoundException("Invalid username or password"));
//
//        if (!customer.getPassword().equals(customerLoginDTO.getPassword())) {
//            throw new RuntimeException("Invalid username or password");
//        }
//
//        LoginResponseDTO response = new LoginResponseDTO();
//        response.setUserId(customer.getId());
//        response.setUsername(customer.getUsername());
//        response.setRole("CUSTOMER");
//        response.setMessage("Login successful");
//        return ResponseEntity.ok(response);
//    }

    @Override
    public CustomerResponseDTO getCustomerById(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + id));
        return mapCustomerToResponseDTO(customer);
    }

    @Override
    @Transactional
    public CustomerResponseDTO updateCustomer(Long id, CustomerCreateDTO customerCreateDTO) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + id));

        // Check for unique constraints if email/username is changed
        if (!customer.getUsername().equals(customerCreateDTO.getUsername()) &&
                customerRepository.findByUsername(customerCreateDTO.getUsername()).isPresent()) {
            throw new RuntimeException("Username already exists.");
        }
        if (!customer.getEmail().equals(customerCreateDTO.getEmail()) &&
                customerRepository.findByEmail(customerCreateDTO.getEmail()).isPresent()) {
            throw new RuntimeException("Email already registered.");
        }


        customer.setUsername(customerCreateDTO.getUsername());
        customer.setPassword(customerCreateDTO.getPassword()); // Storing plain text password
        customer.setAddress(customerCreateDTO.getAddress());
        customer.setPhone(customerCreateDTO.getPhone());
        customer.setEmail(customerCreateDTO.getEmail());

        Customer updatedCustomer = customerRepository.save(customer);
        return mapCustomerToResponseDTO(updatedCustomer);
    }

    @Override
    public void deleteCustomer(Long id) {
        if (!customerRepository.existsById(id)) {
            throw new ResourceNotFoundException("Customer not found with id: " + id);
        }
        customerRepository.deleteById(id);
    }

    @Override
    public List<FoodItemResponseDTO> searchFoodItems(String query) {
        List<FoodItem> foodItems = foodItemRepository.findByNameContainingIgnoreCase(query);
        return foodItems.stream()
                .map(this::mapFoodItemToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<RestaurantResponseDTO> searchRestaurants(String query) {
        List<Restaurant> restaurants = restaurantRepository.findByNameContainingIgnoreCase(query);
        return restaurants.stream()
                .map(this::mapRestaurantToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<OrderResponseDTO> getCustomerPreviousOrders(Long customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + customerId));
        List<Order> orders = orderRepository.findByCustomerId(customerId);
        return orders.stream()
                .map(OrderServiceImpl::mapOrderToResponseDTO) // Re-use OrderServiceImpl's mapper
                .collect(Collectors.toList());
    }

    @Override
    public OrderResponseDTO getCustomerOrderStatus(Long customerId, Long orderId) {
        Order order = orderRepository.findByCustomerIdAndId(customerId, orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found for customer or invalid order ID."));
        return OrderServiceImpl.mapOrderToResponseDTO(order);
    }

    @Override
    public List<RestaurantResponseDTO> getAllRestaurants() {
        List<Restaurant> restaurants = restaurantRepository.findAll();
        return restaurants.stream()
                .map(this::mapRestaurantToResponseDTO)
                .collect(Collectors.toList());
    }


    // --- Helper mapping methods ---
    private CustomerResponseDTO mapCustomerToResponseDTO(Customer customer) {
        CustomerResponseDTO dto = new CustomerResponseDTO();
        dto.setId(customer.getId());
        dto.setUsername(customer.getUsername());
        dto.setAddress(customer.getAddress());
        dto.setPhone(customer.getPhone());
        dto.setEmail(customer.getEmail());
        return dto;
    }

    private FoodItemResponseDTO mapFoodItemToResponseDTO(FoodItem foodItem) {
        FoodItemResponseDTO dto = new FoodItemResponseDTO();
        dto.setId(foodItem.getId());
        dto.setName(foodItem.getName());
        dto.setDescription(foodItem.getDescription());
        dto.setPrice(foodItem.getPrice());
        dto.setImageUrl(foodItem.getImageUrl());
        dto.setCategory(foodItem.getCategory());
        if (foodItem.getRestaurant() != null) {
            dto.setRestaurantId(foodItem.getRestaurant().getId());
            dto.setRestaurantName(foodItem.getRestaurant().getName());
        }
        return dto;
    }

    private RestaurantResponseDTO mapRestaurantToResponseDTO(Restaurant restaurant) {
        RestaurantResponseDTO dto = new RestaurantResponseDTO();
        dto.setId(restaurant.getId());
        dto.setName(restaurant.getName());
        dto.setOwnerName(restaurant.getOwnerName());
        dto.setEmail(restaurant.getEmail());
        dto.setPhone(restaurant.getPhone());
        dto.setAddress(restaurant.getAddress());
        dto.setRegisteredDate(restaurant.getRegisteredDate());
        return dto;
    }
}