package com.example.moneykeeper.dto;

public class ExpenseRequest {
    private int amount;
    private String name;
    private String date;
    private int budgetId;

    public int getAmount() {
        return amount;
    }

    public String getName() {
        return name;
    }

    public String getDate() {
        return date;
    }

    public int getBudgetId() {
        return budgetId;
    }
}
