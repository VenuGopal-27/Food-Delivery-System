package com.cts.fds.controller;

import com.cts.fds.dto.LoginResponseDTO;
import com.cts.fds.dto.DeliveryAgentCreateDTO;
import com.cts.fds.dto.DeliveryAgentLoginDTO;
import com.cts.fds.dto.DeliveryAgentResponseDTO;
import com.cts.fds.dto.OrderResponseDTO;
import com.cts.fds.dto.OrderUpdateStatusDTO;
import com.cts.fds.entity.DeliveryAgent;
import com.cts.fds.service.DeliveryAgentService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/api/deliveryagents")
public class DeliveryAgentController {
    @Autowired private DeliveryAgentService deliveryAgentService;



    @GetMapping("/{agentId}")
    public ResponseEntity<DeliveryAgentResponseDTO> getDeliveryAgentById(@PathVariable Long agentId) {
        System.out.println("Fetching delivery agent with ID: " + agentId);
        DeliveryAgentResponseDTO agent = deliveryAgentService.getDeliveryAgentById(agentId);
        return ResponseEntity.ok(agent);
    }

    @PutMapping("/{agentId}")
    public ResponseEntity<DeliveryAgentResponseDTO> updateDeliveryAgent(@PathVariable Long agentId, @Valid @RequestBody DeliveryAgentCreateDTO agentCreateDTO) {
        DeliveryAgentResponseDTO updatedAgent = deliveryAgentService.updateDeliveryAgent(agentId, agentCreateDTO);
        return ResponseEntity.ok(updatedAgent);
    }

    @DeleteMapping("/{agentId}")
    public ResponseEntity<Void> deleteDeliveryAgent(@PathVariable Long agentId) {
        deliveryAgentService.deleteDeliveryAgent(agentId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{agentId}/assignments")
    public ResponseEntity<List<OrderResponseDTO>> getDeliveryAgentAssignments(@PathVariable Long agentId) {
        List<OrderResponseDTO> assignments = deliveryAgentService.getDeliveryAgentAssignments(agentId);
        return ResponseEntity.ok(assignments);
    }

    @PutMapping("/{agentId}/orders/{orderId}/status")
    public ResponseEntity<OrderResponseDTO> updateDeliveryStatus(
            @PathVariable Long agentId,
            @PathVariable Long orderId,
            @Valid @RequestBody OrderUpdateStatusDTO statusDTO) {
        OrderResponseDTO updatedOrder = deliveryAgentService.updateDeliveryStatus(agentId, orderId, statusDTO);
        return ResponseEntity.ok(updatedOrder);
    }

    @PutMapping("/{agentId}/availability")
    public ResponseEntity<DeliveryAgentResponseDTO> updateAvailabilityStatus(
            @PathVariable Long agentId,
            @RequestParam @NotNull(message = "Availability status cannot be null") DeliveryAgent.AvailabilityStatus status) {
        DeliveryAgentResponseDTO updatedAgent = deliveryAgentService.updateAvailabilityStatus(agentId, status);
        return ResponseEntity.ok(updatedAgent);
    }
}