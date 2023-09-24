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

import java.text.SimpleDateFormat;
import java.time.LocalTime;
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
        return budgetRepository.findAll();
    }

    @PostMapping("/budgets")
    public Budget addBudget(@RequestBody BudgetRequest budgetRequest) {
        UserDetailsImpl principal = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Optional<User> user = userRepository.findUserByUsername(principal.getUsername());
        
        Budget budget = new Budget(
                budgetRequest.getColor(),
                new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(LocalTime.now()),
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
