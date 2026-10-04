package com.first.expensetracker.service;

import com.first.expensetracker.model.User;
import com.first.expensetracker.repository.UserRepository;
import org.springframework.stereotype.Service;
import java.util.Optional;
import java.util.UUID;
import java.math.BigDecimal;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User registerUser(String email, String password) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new RuntimeException("Email already registered");
        }

        User user = new User();
        user.setEmail(email);
        user.setPassword(password); // In a real app, hash this!
        user.setMonthlyBudget(BigDecimal.ZERO);

        return userRepository.save(user);
    }

    public Optional<User> login(String email, String password) {
        return userRepository.findByEmail(email)
                .filter(user -> user.getPassword().equals(password));
    }

    public User getUserById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public User updateBudget(UUID userId, BigDecimal newBudget) {
        User user = getUserById(userId);
        user.setMonthlyBudget(newBudget);
        return userRepository.save(user);
    }
}
