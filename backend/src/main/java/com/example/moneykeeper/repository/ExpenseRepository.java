package com.example.moneykeeper.repository;

import com.example.moneykeeper.entity.Budget;
import com.example.moneykeeper.entity.Expense;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExpenseRepository extends JpaRepository<Expense, Integer> {
    List<Expense> findExpensesByBudgetId(int id);
}
