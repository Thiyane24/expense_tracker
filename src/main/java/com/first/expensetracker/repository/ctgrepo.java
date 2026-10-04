package com.first.expensetracker.repository;

import java.util.UUID;
import com.first.expensetracker.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ctgrepo extends JpaRepository<Category, UUID> {
}
