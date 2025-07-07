package com.cts.fds.service;

import com.cts.fds.dto.*;

import java.util.List;

public interface RestaurantService {
    RestaurantResponseDTO getRestaurantById(Long id);
    RestaurantResponseDTO updateRestaurant(Long id, RestaurantCreateDTO restaurantCreateDTO);
    void deleteRestaurant(Long id);
    FoodItemResponseDTO addFoodItem(Long restaurantId, FoodItemCreateUpdateDTO foodItemDTO);
    FoodItemResponseDTO updateFoodItem(Long restaurantId, Long foodItemId, FoodItemCreateUpdateDTO foodItemDTO);
    void deleteFoodItem(Long restaurantId, Long foodItemId);
    List<FoodItemResponseDTO> getRestaurantMenu(Long restaurantId);
    List<OrderResponseDTO> getRestaurantOrders(Long restaurantId);
    OrderResponseDTO updateOrderStatus(Long restaurantId, Long orderId, OrderUpdateStatusDTO statusDTO);
    OrderResponseDTO assignOrderToDeliveryAgent(Long restaurantId, Long orderId, Long deliveryAgentId);
    
    List<RestaurantResponseDTO> getAllRestaurants();
}