package ru.hse.online.AuthService.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/main")
public class HelloController {
    @GetMapping("/home")
    public String greet(HttpServletRequest request) throws IOException {
        return "Hii";
    }

    @GetMapping("/safe")
    public String noauth(HttpServletRequest request) throws IOException {
        return "Hello";
    }
}
