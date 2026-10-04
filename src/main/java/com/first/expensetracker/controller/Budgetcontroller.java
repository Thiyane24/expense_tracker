package com.first.expensetracker.controller;

import com.first.expensetracker.dto.request.response.BudgetSummaryResponse;
import com.first.expensetracker.service.BudgetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/budgets")
@CrossOrigin(origins = "*") // Permite chamadas do React (CORS)
public class Budgetcontroller {

    @Autowired
    private final BudgetService budgetService;

    public Budgetcontroller(BudgetService budgetService) {
        this.budgetService = budgetService;
    }

    @GetMapping("/summary")
    public ResponseEntity<List<BudgetSummaryResponse>> buscarResumoMensal() {
        List<BudgetSummaryResponse> resumo = budgetService.obterResumoOrcamentos();
        return ResponseEntity.ok(resumo);
    }
}