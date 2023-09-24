package com.example.moneykeeper.controller;

import org.springframework.web.bind.annotation.*;

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
