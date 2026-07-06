package ch.zli.m223.entity;

import ch.zli.m223.entity.enums.UserRole;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;

@Entity
@Table(name = "app_user")
public class User extends PanacheEntity {

    @Column(length = 100)
    public String firstName;

    @Column(length = 100)
    public String lastName;

    @Column(nullable = false, unique = true, length = 120)
    public String email;

    @JsonIgnore
    @Column(length = 128)
    public String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    public UserRole role;

    @Column(nullable = false)
    public boolean active;
}
