package ch.zli.m223.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "trip")
public class Trip extends PanacheEntity {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_id", nullable = false, unique = true)
    public Reservation reservation;

    @Column(nullable = false)
    public int startMileage;

    @Column(nullable = false)
    public int endMileage;

    @Column(length = 255)
    public String startLocation;

    @Column(length = 255)
    public String destination;

    @Column(length = 1000)
    public String notes;
}
