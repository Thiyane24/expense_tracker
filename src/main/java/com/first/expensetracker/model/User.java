package com.first.expensetracker.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;
import java.math.BigDecimal;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private BigDecimal monthlyBudget = BigDecimal.ZERO;

    private LocalDateTime createdAt = LocalDateTime.now();

    // Construtor Padrão
    public User() {}

    // Construtor Completo
    public User(UUID id, String email, String password, BigDecimal monthlyBudget, LocalDateTime createdAt) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.monthlyBudget = monthlyBudget;
        this.createdAt = createdAt;
    }

    // Getters e Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public BigDecimal getMonthlyBudget() { return monthlyBudget; }
    public void setMonthlyBudget(BigDecimal monthlyBudget) { this.monthlyBudget = monthlyBudget; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
