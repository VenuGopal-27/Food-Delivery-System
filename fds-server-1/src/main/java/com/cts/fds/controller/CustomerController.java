package com.cts.fds.controller;

import com.cts.fds.dto.CartAddItemDTO;
import com.cts.fds.dto.CartResponseDTO;
import com.cts.fds.dto.CustomerCreateDTO;
import com.cts.fds.dto.CustomerResponseDTO;
import com.cts.fds.dto.FoodItemResponseDTO;
import com.cts.fds.dto.OrderCreateDTO;
import com.cts.fds.dto.OrderResponseDTO;
import com.cts.fds.dto.RestaurantResponseDTO;
import com.cts.fds.service.CartService;
import com.cts.fds.service.CustomerService;
import com.cts.fds.service.OrderService;

import com.cts.fds.service.RestaurantService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/api/customers")
public class CustomerController {
    @Autowired private CustomerService customerService;
    @Autowired private CartService cartService;
    @Autowired private OrderService orderService;
    @Autowired private RestaurantService restaurantService;



    @GetMapping("/{customerId}")
    public ResponseEntity<CustomerResponseDTO> getCustomerById(@PathVariable Long customerId) {
        CustomerResponseDTO customer = customerService.getCustomerById(customerId);
        return ResponseEntity.ok(customer);
    }

    @PutMapping("/{customerId}")
    public ResponseEntity<CustomerResponseDTO> updateCustomer(@PathVariable Long customerId, @Valid @RequestBody CustomerCreateDTO customerCreateDTO) {
        CustomerResponseDTO updatedCustomer = customerService.updateCustomer(customerId, customerCreateDTO);
        return ResponseEntity.ok(updatedCustomer);
    }

    @DeleteMapping("/{customerId}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable Long customerId) {
        customerService.deleteCustomer(customerId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search/fooditems")
    public ResponseEntity<List<FoodItemResponseDTO>> searchFoodItems(@RequestParam String query) {
        List<FoodItemResponseDTO> foodItems = customerService.searchFoodItems(query);
        return ResponseEntity.ok(foodItems);
    }

    @GetMapping("/search/restaurants")
    public ResponseEntity<List<RestaurantResponseDTO>> searchRestaurants(@RequestParam String query) {
        List<RestaurantResponseDTO> restaurants = customerService.searchRestaurants(query);
        return ResponseEntity.ok(restaurants);
    }

    @GetMapping("/{customerId}/orders")
    public ResponseEntity<List<OrderResponseDTO>> getCustomerPreviousOrders(@PathVariable Long customerId) {
        List<OrderResponseDTO> orders = customerService.getCustomerPreviousOrders(customerId);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/{customerId}/orders/{orderId}/status")
    public ResponseEntity<OrderResponseDTO> getCustomerOrderStatus(@PathVariable Long customerId, @PathVariable Long orderId) {
        OrderResponseDTO order = customerService.getCustomerOrderStatus(customerId, orderId);
        return ResponseEntity.ok(order);
    }

    // Cart Endpoints
    @GetMapping("/{customerId}/cart")
    public ResponseEntity<CartResponseDTO> getCart(@PathVariable Long customerId) {
        CartResponseDTO cart = cartService.getCartByCustomerId(customerId);
        return ResponseEntity.ok(cart);
    }

    @PostMapping("/{customerId}/cart/add")
    public ResponseEntity<CartResponseDTO> addItemToCart(@PathVariable Long customerId, @Valid @RequestBody CartAddItemDTO cartAddItemDTO) {
        cartAddItemDTO.setCustomerId(customerId);
        CartResponseDTO updatedCart = cartService.addItemToCart(cartAddItemDTO);
        return ResponseEntity.ok(updatedCart);
    }

    @PutMapping("/{customerId}/cart/update/{foodItemId}")
    public ResponseEntity<CartResponseDTO> updateCartItemQuantity(
            @PathVariable Long customerId,
            @PathVariable Long foodItemId,
            @RequestParam @Min(value = 0, message = "Quantity cannot be negative") int quantity) {
        CartResponseDTO updatedCart = cartService.updateCartItemQuantity(customerId, foodItemId, quantity);
        return ResponseEntity.ok(updatedCart);
    }



    @DeleteMapping("/{customerId}/cart/remove/{foodItemId}")
    public ResponseEntity<Void> removeCartItem(@PathVariable Long customerId, @PathVariable Long foodItemId) {
        cartService.removeCartItem(customerId, foodItemId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{customerId}/cart/clear")
    public ResponseEntity<Void> clearCart(@PathVariable Long customerId) {
        cartService.clearCart(customerId);
        return ResponseEntity.noContent().build();
    }
    

    // Order Placement from Cart
    @PostMapping("/{customerId}/orders/place")
    public ResponseEntity<OrderResponseDTO> placeOrder(@PathVariable Long customerId, @Valid @RequestBody OrderCreateDTO orderCreateDTO) {
        orderCreateDTO.setCustomerId(customerId); // Ensure customer ID is set from path
        OrderResponseDTO newOrder = orderService.placeOrder(orderCreateDTO);
        return new ResponseEntity<>(newOrder, HttpStatus.CREATED);
    }

    @GetMapping("/restaurants")
    public ResponseEntity<List<RestaurantResponseDTO>> getAllRestaurants() {
        List<RestaurantResponseDTO> restaurants = customerService.getAllRestaurants();
        return ResponseEntity.ok(restaurants);
    }

    @GetMapping("/restaurants/{restaurantId}")
    public ResponseEntity<RestaurantResponseDTO> getRestaurantById(@PathVariable Long restaurantId) {
        RestaurantResponseDTO restaurant = restaurantService.getRestaurantById(restaurantId);
        return ResponseEntity.ok(restaurant);
    }

    @GetMapping("/{restaurantId}/menu")
    public ResponseEntity<List<FoodItemResponseDTO>> getRestaurantMenu(@PathVariable Long restaurantId) {
        List<FoodItemResponseDTO> menu = restaurantService.getRestaurantMenu(restaurantId);
        return ResponseEntity.ok(menu);
    }
}