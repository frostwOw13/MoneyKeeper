package com.example.moneykeeper.controller;

import com.example.moneykeeper.UserDetailsImpl;
import com.example.moneykeeper.entity.Budget;
import com.example.moneykeeper.entity.Expense;
import com.example.moneykeeper.record.ExpenseRecord;
import com.example.moneykeeper.repository.BudgetRepository;
import com.example.moneykeeper.repository.ExpenseRepository;
import com.example.moneykeeper.repository.UserRepository;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class ExpenseController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ExpenseRepository expenseRepository;

    @Autowired
    private BudgetRepository budgetRepository;
    
    @GetMapping("/expenses")
    public List<Expense> getAllExpenses() {
        UserDetailsImpl principal = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        List<Budget> budgets = budgetRepository.findBudgetsByUserId(principal.getId());

        List<Expense> expenses = new ArrayList<>();
        for (Budget budget : budgets) {
            expenses.addAll(expenseRepository.findExpensesByBudgetId(budget.getId()));
        }

        return expenses;
    }

    @GetMapping("/expenses/{budgetId}")
    public List<Expense> getAllExpensesByBudget(@PathVariable int budgetId) {
        return expenseRepository.findExpensesByBudgetId(budgetId);
    }

    @PostMapping("/expenses")
    public Expense addExpense(@RequestBody ExpenseRecord payload, HttpServletResponse response) throws IOException {
        Optional<Budget> budget = budgetRepository.findById(payload.budgetId());

        Expense expense = new Expense();
        try {
            expense.setName(payload.name());
            expense.setAmount(payload.amount());
            expense.setDate(LocalDate.now());
            expense.setBudget(budget.orElse(null));

            expenseRepository.save(expense);
        } catch (DataIntegrityViolationException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        }

        return expense;
    }

    @DeleteMapping("/expenses/{id}")
    public String deleteExpense(@PathVariable int id) {
        expenseRepository.deleteById(id);
        return "Expense with id = " + id + " successfully deleted!";
    }
}