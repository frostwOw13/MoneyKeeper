package com.example.moneykeeper.controller;

import com.example.moneykeeper.UserDetailsImpl;
import com.example.moneykeeper.entity.Budget;
import com.example.moneykeeper.entity.User;
import com.example.moneykeeper.error.ErrorPresentation;
import com.example.moneykeeper.record.BudgetRecord;
import com.example.moneykeeper.repository.BudgetRepository;
import com.example.moneykeeper.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("api/budgets")
public class BudgetController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BudgetRepository budgetRepository;

    @GetMapping
    public ResponseEntity<List<Budget>> getAllBudgets() {
        UserDetailsImpl principal = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(budgetRepository.findBudgetsByUserId(principal.getId()));
    }

    @GetMapping("{id}")
    public ResponseEntity<Budget> getBudgetById(@PathVariable int id) {
        UserDetailsImpl principal = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseEntity.of(budgetRepository.findBudgetsByIdAndUserId(id, principal.getId()));
    }

    @PostMapping
    public ResponseEntity<?> addBudget(
            @RequestBody BudgetRecord payload,
            UriComponentsBuilder uriComponentsBuilder
    ) {
        UserDetailsImpl principal = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Optional<User> user = userRepository.findUserById(principal.getId());

        if (user.isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new ErrorPresentation(List.of(
                            "Session expired"
                    )));
        } else if (payload.name() == null || payload.name().isBlank()) {
            return ResponseEntity
                    .badRequest()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new ErrorPresentation(List.of(
                            "Name of budget should be present"
                    )));
        } else {
            var budget = new Budget(
                    payload.color(),
                    LocalDate.now(),
                    payload.name(),
                    payload.amount(),
                    user.get()
            );

            budgetRepository.save(budget);
            return ResponseEntity
                    .created(uriComponentsBuilder
                            .path("/api/budgets/{id}")
                            .build(Map.of("id", budget.getId())))
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(budget);
        }
    }

    @DeleteMapping("{id}")
    public ResponseEntity<?> deleteBudget(@PathVariable int id) {
        try {
            budgetRepository.deleteById(id);
            return ResponseEntity
                    .ok("Budget with id = " + id + " successfully deleted!");
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new ErrorPresentation(List.of(e.getMessage())));
        }
    }
}
