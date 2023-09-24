package com.example.moneykeeper.controller;

import com.example.moneykeeper.UserDetailsImpl;
import com.example.moneykeeper.dto.ExpenseRequest;
import com.example.moneykeeper.entity.Budget;
import com.example.moneykeeper.entity.Expense;
import com.example.moneykeeper.repository.BudgetRepository;
import com.example.moneykeeper.repository.ExpenseRepository;
import com.example.moneykeeper.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class ExpenseController {

    @Autowired
    private ExpenseRepository expenseRepository;

    @Autowired
    private BudgetRepository budgetRepository;
    
    @GetMapping("/expenses")
    public List<Expense> getAllExpenses() {
        return expenseRepository.findAll();
    }

    @PostMapping("/expenses")
    public Expense addExpense(@RequestBody ExpenseRequest expenseRequest) {
        Optional<Budget> budget = budgetRepository.findById(expenseRequest.getBudgetId());

        Expense expense = new Expense(
                expenseRequest.getName(),
                expenseRequest.getAmount(),
                new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(LocalTime.now()),
                budget.orElse(null)
        );

        expenseRepository.save(expense);
        return expense;
    }

    @DeleteMapping("/expenses/{id}")
    public String deleteExpense(@PathVariable int id) {
        expenseRepository.deleteById(id);
        return "Expense with id = " + id + " successfully deleted!";
    }
}