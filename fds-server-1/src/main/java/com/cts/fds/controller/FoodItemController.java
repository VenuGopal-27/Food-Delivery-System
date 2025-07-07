package com.cts.fds.controller;

import com.cts.fds.dto.FoodItemResponseDTO;
import com.cts.fds.service.FoodItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/api/fooditems")
public class FoodItemController {
    @Autowired private FoodItemService foodItemService;

    @GetMapping
    public ResponseEntity<List<FoodItemResponseDTO>> getAllFoodItems() {
        List<FoodItemResponseDTO> foodItems = foodItemService.getAllFoodItems();
        return ResponseEntity.ok(foodItems);
    }

    @GetMapping("/{foodItemId}")
    public ResponseEntity<FoodItemResponseDTO> getFoodItemById(@PathVariable Long foodItemId) {
        FoodItemResponseDTO foodItem = foodItemService.getFoodItemById(foodItemId);
        return ResponseEntity.ok(foodItem);
    }
}