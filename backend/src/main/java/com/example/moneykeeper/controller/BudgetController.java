package com.example.moneykeeper.controller;

import com.example.moneykeeper.UserDetailsImpl;
import com.example.moneykeeper.dto.BudgetRequest;
import com.example.moneykeeper.entity.Budget;
import com.example.moneykeeper.entity.User;
import com.example.moneykeeper.repository.BudgetRepository;
import com.example.moneykeeper.repository.UserRepository;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class BudgetController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BudgetRepository budgetRepository;

    @GetMapping("/budgets")
    public List<Budget> getAllBudgets() {
        UserDetailsImpl principal = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        return budgetRepository.findBudgetsByUserId(principal.getId());
    }

    @GetMapping("/budgets/{id}")
    public Budget getBudgetById(@PathVariable int id) {
        UserDetailsImpl principal = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        return budgetRepository.findBudgetsByIdAndUserId(id, principal.getId());
    }

    @PostMapping("/budgets")
    public Budget addBudget(@RequestBody BudgetRequest budgetRequest, HttpServletResponse response) throws IOException {
        UserDetailsImpl principal = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Optional<User> user = userRepository.findUserById(principal.getId());

        Budget budget = new Budget();
        try {
            budget.setColor(budgetRequest.getColor());
            budget.setDate(LocalDate.now());
            budget.setName(budgetRequest.getName());
            budget.setAmount(budgetRequest.getAmount());
            budget.setUser(user.orElse(null));

            budgetRepository.save(budget);
        } catch (DataIntegrityViolationException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        }

        return budget;
    }

    @DeleteMapping("/budgets/{id}")
    public String deleteBudget(@PathVariable int id) {
        budgetRepository.deleteById(id);
        return "Budget with id = " + id + " successfully deleted!";
    }
}
