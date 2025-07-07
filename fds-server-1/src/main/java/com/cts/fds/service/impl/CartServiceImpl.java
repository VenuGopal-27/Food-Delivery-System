package com.cts.fds.service.impl;

import com.cts.fds.dto.CartAddItemDTO;
import com.cts.fds.dto.CartItemResponseDTO;
import com.cts.fds.dto.CartResponseDTO;
import com.cts.fds.entity.Cart;
import com.cts.fds.entity.CartItem;
import com.cts.fds.entity.Customer;
import com.cts.fds.entity.FoodItem;
import com.cts.fds.exception.ResourceNotFoundException;
import com.cts.fds.repository.CartItemRepository;
import com.cts.fds.repository.CartRepository;
import com.cts.fds.repository.CustomerRepository;
import com.cts.fds.repository.FoodItemRepository;
import com.cts.fds.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private FoodItemRepository foodItemRepository;
    @Autowired
    private CartItemRepository cartItemRepository;

    @Override
    public CartResponseDTO getCartByCustomerId(Long customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + customerId));

        Cart cart = cartRepository.findByCustomerId(customer.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found for customer id: " + customerId));

        return mapCartToResponseDTO(cart);
    }

    @Override
    @Transactional
    public CartResponseDTO addItemToCart(CartAddItemDTO cartAddItemDTO) {
        Customer customer = customerRepository.findById(cartAddItemDTO.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + cartAddItemDTO.getCustomerId()));

        FoodItem foodItem = foodItemRepository.findById(cartAddItemDTO.getFoodItemId())
                .orElseThrow(() -> new ResourceNotFoundException("Food Item not found with id: " + cartAddItemDTO.getFoodItemId()));

        Cart cart = cartRepository.findByCustomerId(customer.getId())
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setCustomer(customer);
                    return cartRepository.save(newCart);
                });

        // Check if item already exists in cart
        Optional<CartItem> existingCartItemOptional = cartItemRepository.findByCartIdAndFoodItemId(cart.getId(), foodItem.getId());

        if (existingCartItemOptional.isPresent()) {
            CartItem existingCartItem = existingCartItemOptional.get();
            existingCartItem.setQuantity(existingCartItem.getQuantity() + cartAddItemDTO.getQuantity());
            cartItemRepository.save(existingCartItem);
        } else {
            // Check if adding item from a different restaurant
            if (cart.getCartItems() != null && !cart.getCartItems().isEmpty()) {
                Long currentRestaurantIdInCart = cart.getCartItems().get(0).getFoodItem().getRestaurant().getId();
                if (!currentRestaurantIdInCart.equals(foodItem.getRestaurant().getId())) {
                    throw new RuntimeException("Cannot add items from different restaurants to the same cart. Please clear your cart first.");
                }
            }

            CartItem newCartItem = new CartItem();
            newCartItem.setCart(cart);
            newCartItem.setFoodItem(foodItem);
            newCartItem.setQuantity(cartAddItemDTO.getQuantity());
            cartItemRepository.save(newCartItem);
            cart.getCartItems().add(newCartItem); // Add to in-memory list
        }
        return mapCartToResponseDTO(cart);
    }

    @Override
    @Transactional
    public CartResponseDTO updateCartItemQuantity(Long customerId, Long foodItemId, int quantity) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + customerId));

        Cart cart = cartRepository.findByCustomerId(customer.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found for customer id: " + customerId));

        CartItem cartItem = cartItemRepository.findByCartIdAndFoodItemId(cart.getId(), foodItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Food Item not found in cart."));

        if (quantity <= 0) {
            cartItemRepository.delete(cartItem); // Remove if quantity is 0 or less
        } else {
            cartItem.setQuantity(quantity);
            cartItemRepository.save(cartItem);
        }

        // Re-fetch cart to ensure the collection is updated (or manage it in memory)
        cart = cartRepository.findByCustomerId(customer.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found after update."));
        return mapCartToResponseDTO(cart);
    }

    @Override
    @Transactional
    public void removeCartItem(Long customerId, Long foodItemId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + customerId));

        Cart cart = cartRepository.findByCustomerId(customer.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found for customer id: " + customerId));

        CartItem cartItem = cartItemRepository.findByCartIdAndFoodItemId(cart.getId(), foodItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Food Item not found in cart."));

        cartItemRepository.delete(cartItem);
    }

    @Override
    @Transactional
    public void clearCart(Long customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + customerId));

        Cart cart = cartRepository.findByCustomerId(customer.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found for customer id: " + customerId));

        cartItemRepository.deleteAll(cart.getCartItems());
        cart.getCartItems().clear(); // Clear the list in memory
        cartRepository.save(cart); // Save the empty cart state
    }

    // --- Helper mapping method ---
    private CartResponseDTO mapCartToResponseDTO(Cart cart) {
        CartResponseDTO dto = new CartResponseDTO();
        dto.setCartId(cart.getId());
        dto.setCustomerId(cart.getCustomer().getId());

        List<CartItemResponseDTO> itemDTOs = new ArrayList<>();
        double totalValue = 0.0;
        if (cart.getCartItems() != null) {
            for (CartItem cartItem : cart.getCartItems()) {
                CartItemResponseDTO itemDTO = new CartItemResponseDTO();
                itemDTO.setCartItemId(cartItem.getId());
                itemDTO.setFoodItemId(cartItem.getFoodItem().getId());
                itemDTO.setFoodItemName(cartItem.getFoodItem().getName());
                itemDTO.setFoodItemPrice(cartItem.getFoodItem().getPrice());
                itemDTO.setQuantity(cartItem.getQuantity());
                itemDTOs.add(itemDTO);
                totalValue += cartItem.getFoodItem().getPrice() * cartItem.getQuantity();
            }
        }
        dto.setCartItems(itemDTOs);
        dto.setTotalCartValue(totalValue);
        return dto;
    }
}