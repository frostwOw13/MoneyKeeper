package com.example.moneykeeper.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/secured")
public class MainController {

    @GetMapping("/user")
    public String userAccess(Principal principal) {
        System.out.println(principal.getName());
        return principal.getName();
    }
}
