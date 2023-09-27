package com.example.moneykeeper.repository;

import com.example.moneykeeper.entity.Budget;
import com.example.moneykeeper.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BudgetRepository extends JpaRepository<Budget, Integer> {
    List<Budget> findBudgetsByUserId(int userId);
    Optional<Budget> findBudgetsByIdAndUserId(int id, int userId);
}
