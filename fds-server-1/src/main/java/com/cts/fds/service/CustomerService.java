package com.cts.fds.service;

import com.cts.fds.dto.LoginResponseDTO;
import com.cts.fds.dto.CustomerCreateDTO;
import com.cts.fds.dto.CustomerLoginDTO;
import com.cts.fds.dto.CustomerResponseDTO;
import com.cts.fds.dto.FoodItemResponseDTO;
import com.cts.fds.dto.OrderResponseDTO;
import com.cts.fds.dto.RestaurantResponseDTO;

import java.util.List;

import org.springframework.http.ResponseEntity;

public interface CustomerService {
//    ResponseEntity<CustomerResponseDTO> registerCustomer(CustomerCreateDTO customerCreateDTO);
//    ResponseEntity<LoginResponseDTO> loginCustomer(CustomerLoginDTO customerLoginDTO);
    CustomerResponseDTO getCustomerById(Long id);
    CustomerResponseDTO updateCustomer(Long id, CustomerCreateDTO customerCreateDTO);
    void deleteCustomer(Long id);
    List<RestaurantResponseDTO> getAllRestaurants();
    List<FoodItemResponseDTO> searchFoodItems(String query);
    List<RestaurantResponseDTO> searchRestaurants(String query);
    List<OrderResponseDTO> getCustomerPreviousOrders(Long customerId);
    OrderResponseDTO getCustomerOrderStatus(Long customerId, Long orderId);
}
