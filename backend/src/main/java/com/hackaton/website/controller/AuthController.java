package com.hackaton.website.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:5173")
public class AuthController {

    @PostMapping("/login")
    public ResponseEntity<String> login() {
        // Якщо запит дійшов сюди, значить Spring Security вже перевірив
        // логін "admin" та пароль "admin123", і вони правильні!
        return ResponseEntity.ok("{\"message\": \"Успішна авторизація\"}");
    }
}