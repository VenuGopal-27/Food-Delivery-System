package com.cts.fds.service.impl;

import com.cts.fds.dto.LoginResponseDTO;
import com.cts.fds.dto.DeliveryAgentCreateDTO;
import com.cts.fds.dto.DeliveryAgentLoginDTO;
import com.cts.fds.dto.DeliveryAgentResponseDTO;
import com.cts.fds.dto.OrderResponseDTO;
import com.cts.fds.dto.OrderUpdateStatusDTO;
import com.cts.fds.entity.DeliveryAgent;
import com.cts.fds.entity.DeliveryAssignment;
import com.cts.fds.entity.Order;
import com.cts.fds.exception.ResourceNotFoundException;
import com.cts.fds.repository.DeliveryAgentRepository;
import com.cts.fds.repository.DeliveryAssignmentRepository;
import com.cts.fds.repository.OrderRepository;
import com.cts.fds.service.DeliveryAgentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DeliveryAgentServiceImpl implements DeliveryAgentService {

    @Autowired
    private DeliveryAgentRepository deliveryAgentRepository;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private DeliveryAssignmentRepository deliveryAssignmentRepository;

    @Override
    public List<DeliveryAgentResponseDTO> getAllDeliveryAgents() {
        List<DeliveryAgent> agents = deliveryAgentRepository.findAll();
        return agents.stream()
                .map(agent -> {
                    DeliveryAgentResponseDTO dto = new DeliveryAgentResponseDTO();
                    dto.setId(agent.getId());
                    dto.setName(agent.getName());
                    dto.setPhone(agent.getPhone());
                    dto.setAvailabilityStatus(agent.getAvailabilityStatus());
                    return dto;
                })
                .collect(Collectors.toList());
    }


    @Override
    public DeliveryAgentResponseDTO getDeliveryAgentById(Long id) {

        DeliveryAgent agent = deliveryAgentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Delivery Agent not found with id: " + id));
        return mapDeliveryAgentToResponseDTO(agent);
    }

    @Override
    @Transactional
    public DeliveryAgentResponseDTO updateDeliveryAgent(Long id, DeliveryAgentCreateDTO agentCreateDTO) {
        DeliveryAgent agent = deliveryAgentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Delivery Agent not found with id: " + id));

        // Check for unique email if changed
        if (!agent.getEmail().equals(agentCreateDTO.getEmail()) &&
                deliveryAgentRepository.findByEmail(agentCreateDTO.getEmail()).isPresent()) {
            throw new RuntimeException("Email already registered for another delivery agent.");
        }

        agent.setName(agentCreateDTO.getName());
        agent.setPhone(agentCreateDTO.getPhone());
        agent.setEmail(agentCreateDTO.getEmail());
        agent.setAddress(agentCreateDTO.getAddress());
        agent.setAge(agentCreateDTO.getAge());
        agent.setPassword(agentCreateDTO.getPassword()); // Storing plain text password

        DeliveryAgent updatedAgent = deliveryAgentRepository.save(agent);
        return mapDeliveryAgentToResponseDTO(updatedAgent);
    }

    @Override
    public void deleteDeliveryAgent(Long id) {
        if (!deliveryAgentRepository.existsById(id)) {
            throw new ResourceNotFoundException("Delivery Agent not found with id: " + id);
        }
        deliveryAgentRepository.deleteById(id);
    }

    @Override
    public List<OrderResponseDTO> getDeliveryAgentAssignments(Long agentId) {
        DeliveryAgent agent = deliveryAgentRepository.findById(agentId)
                .orElseThrow(() -> new ResourceNotFoundException("Delivery Agent not found with id: " + agentId));

        List<Order> orders = orderRepository.findByDeliveryAssignment_DeliveryAgentId(agentId);
        return orders.stream()
                .map(OrderServiceImpl::mapOrderToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public OrderResponseDTO updateDeliveryStatus(Long agentId, Long orderId, OrderUpdateStatusDTO statusDTO) {
        DeliveryAgent agent = deliveryAgentRepository.findById(agentId)
                .orElseThrow(() -> new ResourceNotFoundException("Delivery Agent not found with id: " + agentId));

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));

        // Ensure the order is assigned to this agent
        if (order.getDeliveryAssignment() == null || !order.getDeliveryAssignment().getDeliveryAgent().getId().equals(agentId)) {
            throw new RuntimeException("Order is not assigned to this delivery agent.");
        }

        // Allowed status transitions for delivery agent
        switch (order.getOrderStatus()) {
            case PREPARED: // Restaurant might have set it to PREPARED
                if (statusDTO.getNewStatus() != Order.OrderStatus.PICKED_UP) {
                    throw new RuntimeException("Invalid status transition from PREPARED: " + statusDTO.getNewStatus());
                }
                // When picked up, update agent status to IN_DELIVERY if not already
                if (agent.getAvailabilityStatus() == DeliveryAgent.AvailabilityStatus.AVAILABLE) {
                    agent.setAvailabilityStatus(DeliveryAgent.AvailabilityStatus.IN_DELIVERY);
                    deliveryAgentRepository.save(agent);
                }
                break;
            case PICKED_UP:
                if (statusDTO.getNewStatus() != Order.OrderStatus.OUT_FOR_DELIVERY) {
                    throw new RuntimeException("Invalid status transition from PICKED_UP: " + statusDTO.getNewStatus());
                }
                break;
            case OUT_FOR_DELIVERY:
                if (statusDTO.getNewStatus() != Order.OrderStatus.DELIVERED) {
                    throw new RuntimeException("Invalid status transition from OUT_FOR_DELIVERY: " + statusDTO.getNewStatus());
                }
                // When delivered, update agent status back to AVAILABLE
                if (agent.getAvailabilityStatus() == DeliveryAgent.AvailabilityStatus.IN_DELIVERY) {
                    agent.setAvailabilityStatus(DeliveryAgent.AvailabilityStatus.AVAILABLE);
                    deliveryAgentRepository.save(agent);
                }
                break;
            default:
                throw new RuntimeException("Cannot update status from " + order.getOrderStatus() + " by delivery agent.");
        }

        order.setOrderStatus(statusDTO.getNewStatus());
        Order updatedOrder = orderRepository.save(order);
        return OrderServiceImpl.mapOrderToResponseDTO(updatedOrder);
    }

    @Override
    @Transactional
    public DeliveryAgentResponseDTO updateAvailabilityStatus(Long agentId, DeliveryAgent.AvailabilityStatus status) {
        DeliveryAgent agent = deliveryAgentRepository.findById(agentId)
                .orElseThrow(() -> new ResourceNotFoundException("Delivery Agent not found with id: " + agentId));

        // Prevent setting to AVAILABLE if currently on an assignment
        if (status == DeliveryAgent.AvailabilityStatus.AVAILABLE &&
                deliveryAssignmentRepository.findByOrderId(agent.getDeliveryAssignments().stream()
                        .filter(da -> da.getOrder().getOrderStatus() != Order.OrderStatus.DELIVERED &&
                                da.getOrder().getOrderStatus() != Order.OrderStatus.CANCELLED)
                        .findFirst()
                        .map(da -> da.getOrder().getId())
                        .orElse(null)).isPresent()) { // Simple check if agent has an active order
            throw new RuntimeException("Cannot set status to AVAILABLE while on an active delivery.");
        }
        // Prevent setting to IN_DELIVERY manually if not assigned
        if (status == DeliveryAgent.AvailabilityStatus.IN_DELIVERY &&
                deliveryAssignmentRepository.findByOrderId(agent.getDeliveryAssignments().stream()
                        .filter(da -> da.getOrder().getOrderStatus() != Order.OrderStatus.DELIVERED &&
                                da.getOrder().getOrderStatus() != Order.OrderStatus.CANCELLED)
                        .findFirst()
                        .map(da -> da.getOrder().getId())
                        .orElse(null)).isEmpty()) {
            throw new RuntimeException("Cannot set status to IN_DELIVERY without an active assignment.");
        }

        agent.setAvailabilityStatus(status);
        DeliveryAgent updatedAgent = deliveryAgentRepository.save(agent);
        return mapDeliveryAgentToResponseDTO(updatedAgent);
    }

    // --- Helper mapping methods ---
    private DeliveryAgentResponseDTO mapDeliveryAgentToResponseDTO(DeliveryAgent agent) {
        DeliveryAgentResponseDTO dto = new DeliveryAgentResponseDTO();
        dto.setId(agent.getId());
        dto.setName(agent.getName());
        dto.setPhone(agent.getPhone());
        dto.setEmail(agent.getEmail());
        dto.setAddress(agent.getAddress());
        dto.setAge(agent.getAge());
        dto.setAvailabilityStatus(agent.getAvailabilityStatus());
        return dto;
    }
}
