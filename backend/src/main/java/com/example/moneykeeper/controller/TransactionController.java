package com.example.moneykeeper.controller;

import com.example.moneykeeper.UserDetailsImpl;
import com.example.moneykeeper.dto.TransactionRequest;
import com.example.moneykeeper.entity.Transaction;
import com.example.moneykeeper.repository.TransactionRepository;
import com.example.moneykeeper.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api")
public class TransactionController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TransactionRepository transactionRepository;


    @GetMapping("/transactions")
    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }

    @PostMapping("/transactions")
    public Transaction addTransaction(@RequestBody TransactionRequest transactionRequest) {
        UserDetailsImpl principal = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Date time = new Date( Long.parseLong(transactionRequest.getDate()) * 1000);
        String dateString = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(time);

        Transaction transaction = new Transaction(
                transactionRequest.getName(),
                transactionRequest.getAmount(),
                dateString,
                userRepository.findUserByUsername(principal.getUsername()).get()
        );

        transactionRepository.save(transaction);
        return transaction;
    }

    @PutMapping("/transactions/{id}")
    public Transaction updateTransaction(@RequestBody TransactionRequest transactionRequest, @PathVariable int id) {
        UserDetailsImpl principal = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Date time = new Date( Long.parseLong(transactionRequest.getDate()) * 1000);
        String dateString = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(time);

        Transaction transaction = new Transaction(
                transactionRequest.getName(),
                transactionRequest.getAmount(),
                dateString,
                userRepository.findUserByUsername(principal.getUsername()).get()
        );
        transaction.setId(id);

        transactionRepository.save(transaction);
        return transaction;
    }

    @DeleteMapping("/transactions/{id}")
    public String deleteTransaction(@PathVariable int id) {
        transactionRepository.deleteById(id);
        return "Transaction with id = " + id + " successfully deleted!";
    }
}