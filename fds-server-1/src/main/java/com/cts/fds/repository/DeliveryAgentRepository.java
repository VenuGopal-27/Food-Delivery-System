package com.cts.fds.repository;

import com.cts.fds.entity.DeliveryAgent;
import com.cts.fds.entity.DeliveryAgent.AvailabilityStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DeliveryAgentRepository extends JpaRepository<DeliveryAgent, Long> {
    Optional<DeliveryAgent> findByEmail(String email);
    List<DeliveryAgent> findByAvailabilityStatus(AvailabilityStatus status);
}