package ch.zli.m223.entity;

import ch.zli.m223.entity.enums.VehicleStatus;
import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;

@Entity
@Table(name = "vehicle")
public class Vehicle extends PanacheEntity {

    @Column(length = 100)
    public String brand;

    @Column(length = 100)
    public String model;

    @Column(nullable = false, unique = true, length = 30)
    public String licensePlate;

    @Column(nullable = false)
    public int mileage;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    public VehicleStatus status;

    @Column(nullable = false)
    public boolean active;
}
