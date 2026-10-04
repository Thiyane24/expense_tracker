package com.first.expensetracker.service;

import com.first.expensetracker.dto.request.response.BudgetSummaryResponse;
import com.first.expensetracker.model.Category;
import com.first.expensetracker.repository.ctgrepo;
import com.first.expensetracker.repository.transactionrepo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class BudgetService {

    private final ctgrepo categoryRepo;
    private final transactionrepo transactionRepo;

    public BudgetService(ctgrepo categoryRepo, transactionrepo transactionRepo) {
        this.categoryRepo = categoryRepo;
        this.transactionRepo = transactionRepo;
    }

    @Transactional(readOnly = true)
    public List<BudgetSummaryResponse> obterResumoOrcamentos() {
        LocalDate hoje = LocalDate.now();
        LocalDate inicioDoMes = hoje.withDayOfMonth(1);
        LocalDate fimDoMes = hoje.withDayOfMonth(hoje.lengthOfMonth());

        List<Category> categorias = categoryRepo.findAll();

        return categorias.stream().map(categoria -> {
            BigDecimal totalGasto = transactionRepo.sumByCategoryIdAndDateBetween(
                    categoria.getId(), inicioDoMes, fimDoMes
            );

            BigDecimal limiteAlerta = categoria.getMonthlyLimit().multiply(new BigDecimal("0.80"));
            boolean pertoDoLimite = totalGasto.compareTo(limiteAlerta) >= 0;

            return new BudgetSummaryResponse(
                    categoria.getMonthlyLimit(),
                    totalGasto,
                    pertoDoLimite
            );
        }).toList();
    }
}