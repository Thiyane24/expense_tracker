package com.first.expensetracker.dto.request.response;

import java.math.BigDecimal;

public record BudgetSummaryResponse(
        BigDecimal monthlyLimit,
        BigDecimal currentMonthSpent,
        boolean isNearLimit // True se passou de 80% do limite
) {}