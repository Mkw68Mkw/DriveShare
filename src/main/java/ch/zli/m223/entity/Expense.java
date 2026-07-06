package ch.zli.m223.entity;

import ch.zli.m223.entity.enums.ExpenseCategory;
import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "expense")
public class Expense extends PanacheEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trip_id", nullable = false)
    public Trip trip;

    @Column(nullable = false, precision = 12, scale = 2)
    public BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    public ExpenseCategory category;

    @Column(length = 500)
    public String description;

    @Column(nullable = false)
    public LocalDate expenseDate;
}
