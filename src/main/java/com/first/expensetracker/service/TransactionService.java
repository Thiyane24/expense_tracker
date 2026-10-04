package com.first.expensetracker.service;

import com.first.expensetracker.dto.request.TransactionRequest;
import com.first.expensetracker.dto.request.response.BudgetSummaryResponse;
import com.first.expensetracker.dto.request.response.TransactionResponse;
import com.first.expensetracker.exception.ResourceNotFoundException;
import com.first.expensetracker.model.Account;
import com.first.expensetracker.model.Category;
import com.first.expensetracker.model.Transaction;
import com.first.expensetracker.repository.AccRepo;
import com.first.expensetracker.repository.ctgrepo;
import com.first.expensetracker.repository.transactionrepo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
public class TransactionService {


    private final transactionrepo transactionRepo;
    private final AccRepo accountRepo;
    private final ctgrepo categoryRepo;

    public TransactionService(transactionrepo transactionRepo, AccRepo accountRepo, ctgrepo categoryRepo) {
        this.transactionRepo = transactionRepo;
        this.accountRepo = accountRepo;
        this.categoryRepo = categoryRepo;
    }


    @Transactional
    public TransactionResponse registrarDespesa(TransactionRequest request) { // CORRIGIDO: Retorna TransactionResponse


        Account account = accountRepo.findById(request.accountId())
                .orElseThrow(() -> new ResourceNotFoundException("Conta não encontrada."));

        Category category = categoryRepo.findById(request.categoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Categoria não encontrada."));


        BigDecimal novoSaldo = account.getBalance().subtract(request.amount());
        account.setBalance(novoSaldo);
        accountRepo.save(account);


        Transaction transaction = new Transaction();
        transaction.setAccount(account);
        transaction.setCategory(category);
        transaction.setAmount(request.amount());
        transaction.setTransactionDate(request.transactionDate());
        transaction.setDescription(request.description());

        Transaction transacaoSalva = transactionRepo.save(transaction);


        LocalDate dataGasto = request.transactionDate();
        LocalDate inicioDoMes = dataGasto.withDayOfMonth(1);
        LocalDate fimDoMes = dataGasto.withDayOfMonth(dataGasto.lengthOfMonth());

        BigDecimal totalGastoNoMes = transactionRepo.sumByCategoryIdAndDateBetween(
                category.getId(), inicioDoMes, fimDoMes
        );

        BigDecimal limiteDeAlerta = category.getMonthlyLimit().multiply(new BigDecimal("0.80"));
        boolean pertoDoLimite = totalGastoNoMes.compareTo(limiteDeAlerta) >= 0;


        BudgetSummaryResponse statusOrcamento = new BudgetSummaryResponse(
                category.getMonthlyLimit(),
                totalGastoNoMes,
                pertoDoLimite
        );


        return new TransactionResponse(
                transacaoSalva.getId(),
                account.getName(),
                category.getName(),
                transacaoSalva.getAmount(),
                transacaoSalva.getTransactionDate(),
                transacaoSalva.getDescription(),
                statusOrcamento
        );

    }
}