package com.example.moneykeeper.controller;

import com.example.moneykeeper.UserDetailsImpl;
import com.example.moneykeeper.dto.BudgetRequest;
import com.example.moneykeeper.entity.Budget;
import com.example.moneykeeper.entity.User;
import com.example.moneykeeper.repository.BudgetRepository;
import com.example.moneykeeper.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
    public Budget addBudget(@RequestBody BudgetRequest budgetRequest) {
        UserDetailsImpl principal = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Optional<User> user = userRepository.findUserById(principal.getId());


        // TODO: add exception if principal is null
        Budget budget = new Budget(
                budgetRequest.getColor(),
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(LocalDateTime.now()),
                budgetRequest.getName(),
                budgetRequest.getAmount(),
                user.orElse(null)
        );

        budgetRepository.save(budget);
        return budget;
    }

    @DeleteMapping("/budgets/{id}")
    public String deleteBudget(@PathVariable int id) {
        budgetRepository.deleteById(id);
        return "Budget with id = " + id + " successfully deleted!";
    }
}
