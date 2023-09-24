package com.example.moneykeeper.dto;

import java.time.LocalDate;

public class TransactionRequest {

    private int amount;
    private String name;
    private String date;

    public int getAmount() {
        return amount;
    }

    public String getName() {
        return name;
    }

    public String getDate() {
        return date;
    }
}
