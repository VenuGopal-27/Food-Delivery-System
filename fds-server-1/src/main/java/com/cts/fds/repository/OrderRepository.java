package com.cts.fds.repository;

import com.cts.fds.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByCustomerId(Long customerId);
    List<Order> findByRestaurantId(Long restaurantId);
    List<Order> findByDeliveryAssignment_DeliveryAgentId(Long agentId); // Find orders assigned to a specific agent
    Optional<Order> findByCustomerIdAndId(Long customerId, Long orderId);
    Optional<Order> findByRestaurantIdAndId(Long restaurantId, Long orderId);
}
