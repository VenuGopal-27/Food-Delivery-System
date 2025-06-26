package com.cts.fds.dto;

import com.cts.fds.entity.FoodItem;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FoodItemResponseDTO {
    private Long id;
    private String name;
    private String description;
    private double price;
    private String imageUrl;
    private FoodItem.FoodCategory category;
    private Long restaurantId;
    private String restaurantName;
}
