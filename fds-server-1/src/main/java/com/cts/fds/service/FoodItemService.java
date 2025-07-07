package com.cts.fds.service;

import com.cts.fds.dto.FoodItemResponseDTO;

import java.util.List;

public interface FoodItemService {
    FoodItemResponseDTO getFoodItemById(Long id);
    List<FoodItemResponseDTO> getAllFoodItems();
}