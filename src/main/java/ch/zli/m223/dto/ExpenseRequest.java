package ch.zli.m223.dto;

import ch.zli.m223.entity.enums.ExpenseCategory;

import java.math.BigDecimal;
import java.time.LocalDate;

public class ExpenseRequest {

    public Long tripId;
    public BigDecimal amount;
    public ExpenseCategory category;
    public String description;
    public LocalDate expenseDate;
}
