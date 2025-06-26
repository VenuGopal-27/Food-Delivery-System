package com.cts.fds.service.impl;

import com.cts.fds.dto.OrderCreateDTO;
import com.cts.fds.dto.OrderItemResponseDTO;
import com.cts.fds.dto.OrderResponseDTO;
import com.cts.fds.entity.Cart;
import com.cts.fds.entity.CartItem;
import com.cts.fds.entity.Customer;
import com.cts.fds.entity.FoodItem;
import com.cts.fds.entity.Order;
import com.cts.fds.entity.OrderItem;
import com.cts.fds.entity.Restaurant;
import com.cts.fds.exception.ResourceNotFoundException;
import com.cts.fds.repository.CartItemRepository;
import com.cts.fds.repository.CartRepository;
import com.cts.fds.repository.CustomerRepository;
import com.cts.fds.repository.OrderRepository;
import com.cts.fds.repository.OrderItemRepository;
import com.cts.fds.repository.RestaurantRepository;
import com.cts.fds.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private CartItemRepository cartItemRepository;
    @Autowired
    private OrderItemRepository orderItemRepository;
    @Autowired
    private RestaurantRepository restaurantRepository;


    @Override
    @Transactional
    public OrderResponseDTO placeOrder(OrderCreateDTO orderCreateDTO) {
        Customer customer = customerRepository.findById(orderCreateDTO.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + orderCreateDTO.getCustomerId()));

        Cart cart = cartRepository.findByCustomerId(customer.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found for customer id: " + customer.getId()));

        if (cart.getCartItems() == null || cart.getCartItems().isEmpty()) {
            throw new RuntimeException("Cart is empty. Cannot place an order.");
        }

        // Validate if all cart items are from the same restaurant
        Restaurant orderRestaurant = null;
        for (CartItem cartItem : cart.getCartItems()) {
            if (orderRestaurant == null) {
                orderRestaurant = cartItem.getFoodItem().getRestaurant();
            } else if (!orderRestaurant.getId().equals(cartItem.getFoodItem().getRestaurant().getId())) {
                throw new RuntimeException("Cannot place an order with food items from multiple restaurants in the same cart.");
            }
        }
        if (orderRestaurant == null) { // Should not happen if cart is not empty
            throw new RuntimeException("Could not determine restaurant for the order.");
        }

        Order order = new Order();
        order.setCustomer(customer);
        order.setRestaurant(orderRestaurant); // Set the restaurant for the order
        order.setDeliveryAddress(orderCreateDTO.getDeliveryAddress());
        order.setPaymentType(orderCreateDTO.getPaymentType());
        order.setOrderDate(LocalDateTime.now());
        order.setOrderStatus(Order.OrderStatus.PENDING); // Initial status

        double totalAmount = 0.0;
        List<OrderItem> orderItems = new ArrayList<>();

        for (CartItem cartItem : cart.getCartItems()) {
            FoodItem foodItem = cartItem.getFoodItem(); // Get food item from cart item
            if (foodItem == null) {
                throw new ResourceNotFoundException("Food item in cart not found.");
            }

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order); // Set the order here, will be persisted with order
            orderItem.setFoodItem(foodItem);
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPricePerItem(foodItem.getPrice()); // Capture price at time of order
            orderItems.add(orderItem);
            totalAmount += foodItem.getPrice() * cartItem.getQuantity();
        }

        order.setTotalAmount(totalAmount);
        order.setOrderItems(orderItems); // Set the list of order items in the order entity

        Order savedOrder = orderRepository.save(order); // This will cascade save OrderItems

        // Clear the cart after order is placed
        cartItemRepository.deleteAll(cart.getCartItems()); // Delete associated cart items first
        cart.getCartItems().clear(); // Clear the in-memory list
        cartRepository.save(cart); // Save the updated cart (now empty)


        return mapOrderToResponseDTO(savedOrder);
    }

    @Override
    public OrderResponseDTO getOrderById(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));
        return mapOrderToResponseDTO(order);
    }

    // --- Helper mapping method (static to be reusable) ---
    public static OrderResponseDTO mapOrderToResponseDTO(Order order) {
        OrderResponseDTO dto = new OrderResponseDTO();
        dto.setId(order.getId());
        dto.setCustomerId(order.getCustomer().getId());
        dto.setCustomerUsername(order.getCustomer().getUsername());
        dto.setRestaurantId(order.getRestaurant().getId());
        dto.setRestaurantName(order.getRestaurant().getName());
        dto.setOrderStatus(order.getOrderStatus());
        dto.setOrderDate(order.getOrderDate());
        dto.setDeliveryAddress(order.getDeliveryAddress());
        dto.setPaymentType(order.getPaymentType());
        dto.setTotalAmount(order.getTotalAmount());

        if (order.getDeliveryAssignment() != null && order.getDeliveryAssignment().getDeliveryAgent() != null) {
            dto.setDeliveryAgentId(order.getDeliveryAssignment().getDeliveryAgent().getId());
            dto.setDeliveryAgentName(order.getDeliveryAssignment().getDeliveryAgent().getName());
        }

        List<OrderItemResponseDTO> itemDTOs = order.getOrderItems().stream()
                .map(orderItem -> {
                    OrderItemResponseDTO itemDTO = new OrderItemResponseDTO();
                    itemDTO.setFoodItemId(orderItem.getFoodItem().getId());
                    itemDTO.setFoodItemName(orderItem.getFoodItem().getName());
                    itemDTO.setFoodItemPrice(orderItem.getPricePerItem());
                    itemDTO.setQuantity(orderItem.getQuantity());
                    return itemDTO;
                })
                .collect(Collectors.toList());
        dto.setOrderItems(itemDTOs);

        return dto;
    }
}