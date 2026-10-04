package com.first.expensetracker.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record TransactionRequest(
        @NotNull(message = "Account ID is required")
        UUID accountId,

        @NotNull(message = "Category ID is required")
        UUID categoryId,

        @NotNull(message = "Amount is required")
        @Positive(message = "Amount must be greater than zero")
        BigDecimal amount,

        @NotNull(message = "Transaction date is required")
        LocalDate transactionDate,

        @NotBlank(message = "Description cannot be empty")
        String description
) {}