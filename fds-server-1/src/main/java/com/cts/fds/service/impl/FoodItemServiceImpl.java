package com.cts.fds.service.impl;

import com.cts.fds.dto.FoodItemResponseDTO;
import com.cts.fds.entity.FoodItem;
import com.cts.fds.exception.ResourceNotFoundException;
import com.cts.fds.repository.FoodItemRepository;
import com.cts.fds.service.FoodItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FoodItemServiceImpl implements FoodItemService {

    @Autowired
    private FoodItemRepository foodItemRepository;

    @Override
    public FoodItemResponseDTO getFoodItemById(Long id) {
        FoodItem foodItem = foodItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Food Item not found with id: " + id));
        return mapFoodItemToResponseDTO(foodItem);
    }

    @Override
    public List<FoodItemResponseDTO> getAllFoodItems() {
        List<FoodItem> foodItems = foodItemRepository.findAll();
        return foodItems.stream()
                .map(this::mapFoodItemToResponseDTO)
                .collect(Collectors.toList());
    }

    // --- Helper mapping method ---
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