package com.cts.fds.controller;

import com.cts.fds.dto.*;
import com.cts.fds.service.DeliveryAgentService;
import com.cts.fds.service.RestaurantService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/api/restaurants")
public class RestaurantController {
    @Autowired private RestaurantService restaurantService;
    @Autowired private DeliveryAgentService deliveryAgentService;



    @GetMapping("/{restaurantId}")
    public ResponseEntity<RestaurantResponseDTO> getRestaurantById(@PathVariable Long restaurantId) {
        RestaurantResponseDTO restaurant = restaurantService.getRestaurantById(restaurantId);
        return ResponseEntity.ok(restaurant);
    }

    @PutMapping("/{restaurantId}")
    public ResponseEntity<RestaurantResponseDTO> updateRestaurant(@PathVariable Long restaurantId, @Valid @RequestBody RestaurantCreateDTO restaurantCreateDTO) {
        RestaurantResponseDTO updatedRestaurant = restaurantService.updateRestaurant(restaurantId, restaurantCreateDTO);
        return ResponseEntity.ok(updatedRestaurant);
    }

    @DeleteMapping("/{restaurantId}")
    public ResponseEntity<Void> deleteRestaurant(@PathVariable Long restaurantId) {
        restaurantService.deleteRestaurant(restaurantId);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/")
    public ResponseEntity<List<RestaurantResponseDTO>> getAllRestaurants() {
        List<RestaurantResponseDTO> restaurants = restaurantService.getAllRestaurants();
        return ResponseEntity.ok(restaurants);
    }
    
    // Food Item Management
    @PostMapping("/{restaurantId}/menu")
    public ResponseEntity<FoodItemResponseDTO> addFoodItem(@PathVariable Long restaurantId, @Valid @RequestBody FoodItemCreateUpdateDTO foodItemDTO) {
        FoodItemResponseDTO newFoodItem = restaurantService.addFoodItem(restaurantId, foodItemDTO);
        return new ResponseEntity<>(newFoodItem, HttpStatus.CREATED);
    }

    @PutMapping("/{restaurantId}/menu/{foodItemId}")
    public ResponseEntity<FoodItemResponseDTO> updateFoodItem(@PathVariable Long restaurantId, @PathVariable Long foodItemId, @Valid @RequestBody FoodItemCreateUpdateDTO foodItemDTO) {
        FoodItemResponseDTO updatedFoodItem = restaurantService.updateFoodItem(restaurantId, foodItemId, foodItemDTO);
        return ResponseEntity.ok(updatedFoodItem);
    }

    @DeleteMapping("/{restaurantId}/menu/{foodItemId}")
    public ResponseEntity<Void> deleteFoodItem(@PathVariable Long restaurantId, @PathVariable Long foodItemId) {
        restaurantService.deleteFoodItem(restaurantId, foodItemId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{restaurantId}/menu")
    public ResponseEntity<List<FoodItemResponseDTO>> getRestaurantMenu(@PathVariable Long restaurantId) {
        List<FoodItemResponseDTO> menu = restaurantService.getRestaurantMenu(restaurantId);
        return ResponseEntity.ok(menu);
    }

    // Order Management
    @GetMapping("/{restaurantId}/orders")
    public ResponseEntity<List<OrderResponseDTO>> getRestaurantOrders(@PathVariable Long restaurantId) {
        List<OrderResponseDTO> orders = restaurantService.getRestaurantOrders(restaurantId);
        return ResponseEntity.ok(orders);
    }

    @PutMapping("/{restaurantId}/orders/{orderId}/status")
    public ResponseEntity<OrderResponseDTO> updateOrderStatus(@PathVariable Long restaurantId, @PathVariable Long orderId, @Valid @RequestBody OrderUpdateStatusDTO statusDTO) {
        OrderResponseDTO updatedOrder = restaurantService.updateOrderStatus(restaurantId, orderId, statusDTO);
        return ResponseEntity.ok(updatedOrder);
    }

    @PostMapping("/{restaurantId}/orders/{orderId}/assign/{agentId}")
    public ResponseEntity<OrderResponseDTO> assignOrderToDeliveryAgent(
            @PathVariable Long restaurantId,
            @PathVariable Long orderId,
            @PathVariable Long agentId) {
        OrderResponseDTO updatedOrder = restaurantService.assignOrderToDeliveryAgent(restaurantId, orderId, agentId);
        return ResponseEntity.ok(updatedOrder);
    }

    @GetMapping("/all")
    public ResponseEntity<List<DeliveryAgentResponseDTO>> getAllDeliveryAgents() {
        List<DeliveryAgentResponseDTO> deliveryAgents = deliveryAgentService.getAllDeliveryAgents();
        return ResponseEntity.ok(deliveryAgents);
    }
}