package com.example.moneykeeper.controller;

import com.example.moneykeeper.UserDetailsImpl;
import com.example.moneykeeper.dto.TransactionRequest;
import com.example.moneykeeper.entity.Transaction;
import com.example.moneykeeper.repository.TransactionRepository;
import com.example.moneykeeper.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.sql.Timestamp;

@RestController
@RequestMapping("/secured")
public class MainController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @GetMapping("/user")
    public String userAccess(Principal principal) {
        System.out.println(principal.getName());
        return principal.getName();
    }

    @PostMapping("/addTransaction")
    public Transaction addTransaction(@RequestBody TransactionRequest transactionRequest) {
        UserDetailsImpl principal = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();


        Transaction transaction = new Transaction(
                transactionRequest.getName(),
                transactionRequest.getAmount(),
                new Timestamp(Long.parseLong(transactionRequest.getDate())),
                userRepository.findUserByUsername(principal.getUsername()).get()
        );

        transactionRepository.save(transaction);
        return transaction;
    }
}
