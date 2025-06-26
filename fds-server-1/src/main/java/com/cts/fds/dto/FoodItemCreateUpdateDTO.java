package com.cts.fds.dto;

import com.cts.fds.entity.FoodItem;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FoodItemCreateUpdateDTO {
    @NotBlank(message = "Food item name cannot be blank")
    private String name;

    private String description;

    @Min(value = 0, message = "Price cannot be negative")
    private double price;

    private String imageUrl;

    @NotNull(message = "Category cannot be null")
    private FoodItem.FoodCategory category;
}
