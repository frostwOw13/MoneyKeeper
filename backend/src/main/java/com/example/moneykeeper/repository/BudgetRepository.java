package com.example.moneykeeper.repository;

import com.example.moneykeeper.entity.Budget;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BudgetRepository extends JpaRepository<Budget, Integer> {
    List<Budget> findBudgetsByUserId(int id);
}
