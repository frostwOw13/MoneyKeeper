package com.example.moneykeeper.repository;

import com.example.moneykeeper.entity.Expense;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExpenseRepository extends JpaRepository<Expense, Integer> {
}
