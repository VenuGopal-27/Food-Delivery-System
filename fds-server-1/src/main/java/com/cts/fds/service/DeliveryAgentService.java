package com.cts.fds.service;

import com.cts.fds.dto.LoginResponseDTO;
import com.cts.fds.dto.DeliveryAgentCreateDTO;
import com.cts.fds.dto.DeliveryAgentLoginDTO;
import com.cts.fds.dto.DeliveryAgentResponseDTO;
import com.cts.fds.dto.OrderResponseDTO;
import com.cts.fds.dto.OrderUpdateStatusDTO;
import com.cts.fds.entity.DeliveryAgent;

import java.util.List;

public interface DeliveryAgentService {
    List<DeliveryAgentResponseDTO> getAllDeliveryAgents();
     DeliveryAgentResponseDTO getDeliveryAgentById(Long id);
    DeliveryAgentResponseDTO updateDeliveryAgent(Long id, DeliveryAgentCreateDTO agentCreateDTO);
    void deleteDeliveryAgent(Long id);
    List<OrderResponseDTO> getDeliveryAgentAssignments(Long agentId);
    OrderResponseDTO updateDeliveryStatus(Long agentId, Long orderId, OrderUpdateStatusDTO statusDTO);
    DeliveryAgentResponseDTO updateAvailabilityStatus(Long agentId, DeliveryAgent.AvailabilityStatus status);
}
