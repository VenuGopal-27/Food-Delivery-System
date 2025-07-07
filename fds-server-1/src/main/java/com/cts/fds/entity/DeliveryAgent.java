package com.cts.fds.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "delivery_agents")
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Setter
public class DeliveryAgent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String phone;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false)
    private int age;

    @Column(nullable = false)
    private String password; // Storing as plain text as per your request (Highly Insecure for production!)

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AvailabilityStatus availabilityStatus; // ENUM: AVAILABLE, IN_DELIVERY, OFFLINE

    @OneToMany(mappedBy = "deliveryAgent", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<DeliveryAssignment> deliveryAssignments;

    public enum AvailabilityStatus {
        AVAILABLE, IN_DELIVERY, OFFLINE
    }
}
