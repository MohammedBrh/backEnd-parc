package com.fleet.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Attribution {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @ManyToOne(optional = false)
    public Car car;

    @ManyToOne(optional = false)
    public Driver driver;

    @ManyToOne(optional = false)
    public Personnel personnel;

    @Column(nullable = false)
    public LocalDateTime startAt;

    @Column(nullable = false)
    public LocalDateTime endAt;

    public int passengers = 1;

    public String destination;

    @Column(length = 1000)
    public String notes;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    public AttributionStatus status = AttributionStatus.ACTIVE;

    public LocalDateTime createdAt = LocalDateTime.now();
}
