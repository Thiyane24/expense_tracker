package com.first.expensetracker.controller;

import com.first.expensetracker.model.User;
import com.first.expensetracker.security.JwtTokenUtil;
import com.first.expensetracker.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {
    private final UserService userService;
    private final JwtTokenUtil jwtTokenUtil;

    public AuthController(UserService userService, JwtTokenUtil jwtTokenUtil) {
        this.userService = userService;
        this.jwtTokenUtil = jwtTokenUtil;
    }

    @PostMapping("/signup")
    public ResponseEntity<?> register(@RequestBody Map<String, String> request) {
        try {
            String email = request.get("email");
            String password = request.get("password");
            User user = userService.registerUser(email, password);
            return ResponseEntity.ok(Map.of(
                "id", user.getId(),
                "email", user.getEmail(),
                "message", "User registered successfully"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String password = request.get("password");

        return userService.login(email, password)
                .map(user -> ResponseEntity.ok(Map.of(
                        "id", user.getId(),
                        "email", user.getEmail(),
                        "budget", user.getMonthlyBudget(),
                        "token", jwtTokenUtil.generateToken(user.getId())
                )))
                .orElse(ResponseEntity.status(401).body(Map.of("error", "Invalid credentials")));
    }

    @GetMapping("/budget")
    public ResponseEntity<?> getBudget(@RequestHeader("Authorization") String authHeader) {
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(401).body(Map.of("error", "Missing token"));
            }

            String token = authHeader.substring(7);
            UUID userId = jwtTokenUtil.validateTokenAndGetUserId(token);

            if (userId == null) {
                return ResponseEntity.status(401).body(Map.of("error", "Invalid token"));
            }

            User user = userService.getUserById(userId);
            return ResponseEntity.ok(Map.of(
                "user", Map.of("name", user.getEmail()),
                "budget", user.getMonthlyBudget()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PatchMapping("/budget")
    public ResponseEntity<?> updateBudget(@RequestHeader("Authorization") String authHeader, @RequestParam BigDecimal amount) {
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(401).body(Map.of("error", "Missing token"));
            }

            String token = authHeader.substring(7);
            UUID userId = jwtTokenUtil.validateTokenAndGetUserId(token);

            if (userId == null) {
                return ResponseEntity.status(401).body(Map.of("error", "Invalid token"));
            }

            User user = userService.updateBudget(userId, amount);
            return ResponseEntity.ok(Map.of("message", "Budget updated", "newBudget", user.getMonthlyBudget()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
