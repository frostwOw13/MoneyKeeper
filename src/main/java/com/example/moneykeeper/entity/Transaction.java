package com.example.moneykeeper.entity;

import jakarta.persistence.*;

import java.sql.Timestamp;
import java.time.LocalDate;

@Entity
@Table(name = "transactions")
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column
    private String name;

    @Column
    private int amount;

    @Column
    private Timestamp date;

    @OneToOne
    private User user;

    public Transaction() {
    }

    public Transaction(String name, int amount, Timestamp date, User userId) {
        this.name = name;
        this.amount = amount;
        this.date = date;
        this.user = userId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public Timestamp getDate() {
        return date;
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }

    public User getUserId() {
        return user;
    }

    public void setUserId(User userId) {
        this.user = userId;
    }
}
