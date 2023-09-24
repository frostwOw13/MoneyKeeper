package com.example.moneykeeper.repository;

import com.example.moneykeeper.entity.Budget;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BudgetRepository extends JpaRepository<Budget, Integer> {

}
