package com.example.moneykeeper.controller;

import com.example.moneykeeper.UserDetailsImpl;
import com.example.moneykeeper.entity.Budget;
import com.example.moneykeeper.entity.Expense;
import com.example.moneykeeper.record.ErrorRecord;
import com.example.moneykeeper.record.ExpenseRecord;
import com.example.moneykeeper.repository.BudgetRepository;
import com.example.moneykeeper.repository.ExpenseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("api/expenses")
public class ExpenseController {

    @Autowired
    private ExpenseRepository expenseRepository;

    @Autowired
    private BudgetRepository budgetRepository;
    
    @GetMapping
    public ResponseEntity<List<Expense>> getAllExpenses() {
        UserDetailsImpl principal = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        List<Budget> budgets = budgetRepository.findBudgetsByUserId(principal.getId());

        List<Expense> expenses = new ArrayList<>();
        for (Budget budget : budgets) {
            expenses.addAll(expenseRepository.findExpensesByBudgetId(budget.getId()));
        }

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(expenses);
    }

    @GetMapping("{id}")
    public ResponseEntity<List<Expense>> getAllExpensesByBudget(@PathVariable int id) {
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(expenseRepository.findExpensesByBudgetId(id));
    }

    @PostMapping
    public ResponseEntity<?> addExpense(@RequestBody ExpenseRecord payload, UriComponentsBuilder uriComponentsBuilder) {
        Optional<Budget> budget = budgetRepository.findById(payload.budgetId());

        if (budget.isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new ErrorRecord(List.of(
                            "Cannot find budget by this id"
                    )));
        }

        if (payload.name() == null || payload.name().isBlank()) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new ErrorRecord(List.of(
                            "Name of expense should be present"
                    )));
        }

        if (payload.amount() == 0) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new ErrorRecord(List.of(
                            "Amount of expense should be present"
                    )));
        }

        Expense expense = new Expense(
                payload.name(),
                payload.amount(),
                LocalDate.now(),
                budget.get()
        );

        expenseRepository.save(expense);
        return ResponseEntity
                .created(uriComponentsBuilder
                        .path("/api/expenses/{id}")
                        .build(Map.of("id", expense.getId())))
                .contentType(MediaType.APPLICATION_JSON)
                .body(expense);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<?> deleteExpense(@PathVariable int id) {
        try {
            expenseRepository.deleteById(id);
            return ResponseEntity
                    .ok("Expense with id = " + id + " successfully deleted!");
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new ErrorRecord(List.of(e.getMessage())));
        }
    }
}