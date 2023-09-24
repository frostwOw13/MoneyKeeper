package com.example.moneykeeper.dto;

public class ExpenseRequest {
    private int amount;
    private String name;
    private int budgetId;

    public int getAmount() {
        return amount;
    }

    public String getName() {
        return name;
    }

    public int getBudgetId() {
        return budgetId;
    }
}
