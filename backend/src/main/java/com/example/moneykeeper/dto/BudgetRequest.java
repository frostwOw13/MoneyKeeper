package com.example.moneykeeper.dto;


public class BudgetRequest {
    private String color;
    private String name;
    private int amount;

    public BudgetRequest() {
    }

    public BudgetRequest(String color, String name, int amount) {
        this.color = color;
        this.name = name;
        this.amount = amount;
    }

    public String getColor() {
        return color;
    }

    public String getName() {
        return name;
    }

    public int getAmount() {
        return amount;
    }
}
