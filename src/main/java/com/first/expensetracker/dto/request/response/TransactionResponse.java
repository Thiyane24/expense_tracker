package com.first.expensetracker.dto.request.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record TransactionResponse(
        UUID id,
        String accountName,
        String categoryName,
        BigDecimal amount,
        LocalDate transactionDate,
        String description,
        BudgetSummaryResponse budgetStatus
) {}