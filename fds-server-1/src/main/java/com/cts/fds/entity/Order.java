package com.cts.fds.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Setter
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id", nullable = false)
    private Restaurant restaurant;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus orderStatus; // ENUM: PENDING, PREPARING, PREPARED, PICKED_UP, OUT_FOR_DELIVERY, DELIVERED, CANCELLED

    @Column(nullable = false)
    private LocalDateTime orderDate;

    @Column(nullable = false)
    private String deliveryAddress;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentType paymentType; // ENUM: CARD, CASH_ON_DELIVERY, UPI

    @Column(nullable = false)
    private double totalAmount; // Calculated based on order items

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<OrderItem> orderItems;

    @OneToOne(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private DeliveryAssignment deliveryAssignment; // Can be null if not yet assigned

    public enum OrderStatus {
        PENDING, PREPARING, PREPARED, PICKED_UP, OUT_FOR_DELIVERY, DELIVERED, CANCELLED
    }

    public enum PaymentType {
        CARD, CASH_ON_DELIVERY, UPI
    }
}