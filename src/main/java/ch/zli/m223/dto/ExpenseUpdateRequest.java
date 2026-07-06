package ch.zli.m223.dto;

import ch.zli.m223.entity.enums.ExpenseCategory;

import java.math.BigDecimal;
import java.time.LocalDate;

public class ExpenseUpdateRequest {

    public BigDecimal amount;
    public ExpenseCategory category;
    public String description;
    public LocalDate expenseDate;
}
