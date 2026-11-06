package com.fleet.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

@Entity
public class Driver {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @NotBlank(message = "Le nom est obligatoire.")
    @Column(nullable = false)
    public String name;

    @NotBlank(message = "Le téléphone est obligatoire.")
    @Pattern(regexp = "^\\+?[0-9]{9,15}$", message = "Numéro de téléphone invalide.")
    @Column(nullable = false)
    public String phone;

    public String licenseNumber;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    public DriverStatus status = DriverStatus.AVAILABLE;

    public boolean active = true;
}
