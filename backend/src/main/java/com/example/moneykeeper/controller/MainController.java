package com.example.moneykeeper.controller;

import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/user")
public class MainController {
    @GetMapping("/")
    public String userAccess(Principal principal) {
        return principal.getName();
    }
}
