package com.cts.fds.service.impl;

import com.cts.fds.dto.*;
import com.cts.fds.entity.DeliveryAgent;
import com.cts.fds.entity.DeliveryAssignment;
import com.cts.fds.entity.FoodItem;
import com.cts.fds.entity.Order;
import com.cts.fds.entity.Restaurant;
import com.cts.fds.exception.ResourceNotFoundException;
import com.cts.fds.repository.DeliveryAgentRepository;
import com.cts.fds.repository.DeliveryAssignmentRepository;
import com.cts.fds.repository.FoodItemRepository;
import com.cts.fds.repository.OrderRepository;
import com.cts.fds.repository.RestaurantRepository;
import com.cts.fds.service.RestaurantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RestaurantServiceImpl implements RestaurantService {

    @Autowired
    private RestaurantRepository restaurantRepository;
    @Autowired
    private FoodItemRepository foodItemRepository;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private DeliveryAgentRepository deliveryAgentRepository;
    @Autowired
    private DeliveryAssignmentRepository deliveryAssignmentRepository;



    @Override
    public RestaurantResponseDTO getRestaurantById(Long id) {
        Restaurant restaurant = restaurantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found with id: " + id));
        return mapRestaurantToResponseDTO(restaurant);
    }

    @Override
    @Transactional
    public RestaurantResponseDTO updateRestaurant(Long id, RestaurantCreateDTO restaurantCreateDTO) {
        Restaurant restaurant = restaurantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found with id: " + id));

        // Check for unique email if changed
        if (!restaurant.getEmail().equals(restaurantCreateDTO.getEmail()) &&
                restaurantRepository.findByEmail(restaurantCreateDTO.getEmail()).isPresent()) {
            throw new RuntimeException("Email already registered for another restaurant.");
        }

        restaurant.setName(restaurantCreateDTO.getName());
        restaurant.setOwnerName(restaurantCreateDTO.getOwnerName());
        restaurant.setPassword(restaurantCreateDTO.getPassword()); // Storing plain text password
        restaurant.setEmail(restaurantCreateDTO.getEmail());
        restaurant.setPhone(restaurantCreateDTO.getPhone());
        restaurant.setAddress(restaurantCreateDTO.getAddress());

        Restaurant updatedRestaurant = restaurantRepository.save(restaurant);
        return mapRestaurantToResponseDTO(updatedRestaurant);
    }

    @Override
    public void deleteRestaurant(Long id) {
        if (!restaurantRepository.existsById(id)) {
            throw new ResourceNotFoundException("Restaurant not found with id: " + id);
        }
        restaurantRepository.deleteById(id);
    }
    
    @Override
    public List<RestaurantResponseDTO> getAllRestaurants() {
        List<Restaurant> restaurants = restaurantRepository.findAll();
        return restaurants.stream()
                .map(this::mapRestaurantToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public FoodItemResponseDTO addFoodItem(Long restaurantId, FoodItemCreateUpdateDTO foodItemDTO) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found with id: " + restaurantId));

        FoodItem foodItem = new FoodItem();
        foodItem.setName(foodItemDTO.getName());
        foodItem.setDescription(foodItemDTO.getDescription());
        foodItem.setPrice(foodItemDTO.getPrice());
        foodItem.setImageUrl(foodItemDTO.getImageUrl());
        foodItem.setCategory(foodItemDTO.getCategory());
        foodItem.setRestaurant(restaurant);

        FoodItem savedFoodItem = foodItemRepository.save(foodItem);
        return mapFoodItemToResponseDTO(savedFoodItem);
    }

    @Override
    @Transactional
    public FoodItemResponseDTO updateFoodItem(Long restaurantId, Long foodItemId, FoodItemCreateUpdateDTO foodItemDTO) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found with id: " + restaurantId));

        FoodItem foodItem = foodItemRepository.findById(foodItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Food Item not found with id: " + foodItemId));

        if (!foodItem.getRestaurant().getId().equals(restaurantId)) {
            throw new RuntimeException("Food item does not belong to this restaurant.");
        }

        foodItem.setName(foodItemDTO.getName());
        foodItem.setDescription(foodItemDTO.getDescription());
        foodItem.setPrice(foodItemDTO.getPrice());
        foodItem.setImageUrl(foodItemDTO.getImageUrl());
        foodItem.setCategory(foodItemDTO.getCategory());

        FoodItem updatedFoodItem = foodItemRepository.save(foodItem);
        return mapFoodItemToResponseDTO(updatedFoodItem);
    }

    @Override
    public void deleteFoodItem(Long restaurantId, Long foodItemId) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found with id: " + restaurantId));

        FoodItem foodItem = foodItemRepository.findById(foodItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Food Item not found with id: " + foodItemId));

        if (!foodItem.getRestaurant().getId().equals(restaurantId)) {
            throw new RuntimeException("Food item does not belong to this restaurant.");
        }

        foodItemRepository.delete(foodItem);
    }

    @Override
    public List<FoodItemResponseDTO> getRestaurantMenu(Long restaurantId) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found with id: " + restaurantId));

        List<FoodItem> menuItems = foodItemRepository.findByRestaurantId(restaurantId);
        return menuItems.stream()
                .map(this::mapFoodItemToResponseDTO)
                .collect(Collectors.toList());
    }


    @Override
    public List<OrderResponseDTO> getRestaurantOrders(Long restaurantId) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found with id: " + restaurantId));

        List<Order> orders = orderRepository.findByRestaurantId(restaurantId);
        return orders.stream()
                .map(OrderServiceImpl::mapOrderToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public OrderResponseDTO updateOrderStatus(Long restaurantId, Long orderId, OrderUpdateStatusDTO statusDTO) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found with id: " + restaurantId));

        Order order = orderRepository.findByRestaurantIdAndId(restaurantId, orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found for this restaurant or invalid Order ID."));

        // Only allowed status transitions for restaurant
        switch (order.getOrderStatus()) {
            case PENDING:
                if (statusDTO.getNewStatus() != Order.OrderStatus.PREPARING && statusDTO.getNewStatus() != Order.OrderStatus.CANCELLED) {
                    throw new RuntimeException("Invalid status transition from PENDING: " + statusDTO.getNewStatus());
                }
                break;
            case PREPARING:
                if (statusDTO.getNewStatus() != Order.OrderStatus.PREPARED && statusDTO.getNewStatus() != Order.OrderStatus.CANCELLED) {
                    throw new RuntimeException("Invalid status transition from PREPARING: " + statusDTO.getNewStatus());
                }
                break;
            case PREPARED:
                if (statusDTO.getNewStatus() != Order.OrderStatus.PICKED_UP) { // Picked_up will be set by delivery agent typically
                    throw new RuntimeException("Invalid status transition from PREPARED: " + statusDTO.getNewStatus());
                }
                break;
            default:
                throw new RuntimeException("Cannot update status from " + order.getOrderStatus() + " by restaurant.");
        }

        order.setOrderStatus(statusDTO.getNewStatus());
        Order updatedOrder = orderRepository.save(order);
        return OrderServiceImpl.mapOrderToResponseDTO(updatedOrder);
    }



    @Override
    @Transactional
    public OrderResponseDTO assignOrderToDeliveryAgent(Long restaurantId, Long orderId, Long deliveryAgentId) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found with id: " + restaurantId));

        Order order = orderRepository.findByRestaurantIdAndId(restaurantId, orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found for this restaurant or invalid Order ID."));

        DeliveryAgent agent = deliveryAgentRepository.findById(deliveryAgentId)
                .orElseThrow(() -> new ResourceNotFoundException("Delivery Agent not found with id: " + deliveryAgentId));

        if (order.getDeliveryAssignment() != null) {
            throw new RuntimeException("Order already assigned to a delivery agent.");
        }
        if (order.getOrderStatus() != Order.OrderStatus.PREPARED) {
             throw new RuntimeException("Order must be in PREPARED status to be assigned for delivery.");
        }
        if (agent.getAvailabilityStatus() != DeliveryAgent.AvailabilityStatus.AVAILABLE) {
            throw new RuntimeException("Delivery agent is not available.");
        }

        DeliveryAssignment assignment = new DeliveryAssignment();
        assignment.setOrder(order);
        assignment.setDeliveryAgent(agent);
        assignment.setRestaurant(restaurant);
        assignment.setAssignedAt(LocalDateTime.now());

        deliveryAssignmentRepository.save(assignment);

        order.setDeliveryAssignment(assignment);
        // Optionally update order status to 'PICKED_UP' by agent later, or 'OUT_FOR_DELIVERY' here
        // For simplicity, let's keep it 'PREPARED' and agent updates to 'PICKED_UP'
        orderRepository.save(order);

        // Update agent status to IN_DELIVERY
        agent.setAvailabilityStatus(DeliveryAgent.AvailabilityStatus.IN_DELIVERY);
        deliveryAgentRepository.save(agent);


        return OrderServiceImpl.mapOrderToResponseDTO(order);
    }


    // --- Helper mapping methods ---
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
}