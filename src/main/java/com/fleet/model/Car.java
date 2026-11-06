package com.fleet.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
public class Car {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @NotBlank(message = "La matricule est obligatoire.")
    @Column(nullable = false, unique = true)
    public String matricule;

    @Min(value = 1, message = "Le nombre de places doit être au moins 1.")
    public int places;

    @NotBlank(message = "Le type est obligatoire.")
    @Column(name = "car_type", nullable = false)
    public String type;

    @NotNull(message = "La catégorie est obligatoire.")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    public CarCategory category;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    public CarStatus status = CarStatus.AVAILABLE;

    /** false = soft-deleted (kept so attribution history stays readable). */
    public boolean active = true;
}
