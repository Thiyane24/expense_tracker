package com.first.expensetracker.repository;

import com.first.expensetracker.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface AccRepo extends JpaRepository<Account, UUID> {
}
