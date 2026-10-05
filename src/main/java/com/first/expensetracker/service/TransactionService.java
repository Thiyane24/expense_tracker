package com.first.expensetracker.service;

import com.first.expensetracker.model.*;
import com.first.expensetracker.repository.*;
import com.first.expensetracker.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

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
    public Map<String, Object> registrarDespesa(Map<String, Object> request) {
        UUID accountId = UUID.fromString((String) request.get("accountId"));
        UUID categoryId = UUID.fromString((String) request.get("categoryId"));
        BigDecimal amount = new BigDecimal(request.get("amount").toString());

        Account account = accountRepo.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Conta não encontrada."));
        Category category = categoryRepo.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Categoria não encontrada."));

        account.setBalance(account.getBalance().subtract(amount));
        accountRepo.save(account);

        Transaction transaction = new Transaction();
        transaction.setAccount(account);
        transaction.setCategory(category);
        transaction.setAmount(amount);
        transaction.setTransactionDate(LocalDate.now());
        transaction.setDescription((String) request.get("description"));

        Transaction saved = transactionRepo.save(transaction);
        return mapToSimpleResponse(saved);
    }

    public List<Map<String, Object>> listAllTransactions() {
        return transactionRepo.findAll().stream()
                .map(this::mapToSimpleResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public Map<String, Object> updateTransaction(UUID id, Map<String, Object> request) {
        Transaction transaction = transactionRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transação não encontrada."));

        BigDecimal oldAmount = transaction.getAmount();
        BigDecimal newAmount = new BigDecimal(request.get("amount").toString());

        Account account = transaction.getAccount();
        account.setBalance(account.getBalance().add(oldAmount).subtract(newAmount));
        accountRepo.save(account);

        transaction.setAmount(newAmount);
        transaction.setDescription((String) request.get("description"));

        return mapToSimpleResponse(transactionRepo.save(transaction));
    }

    @Transactional
    public void deleteTransaction(UUID id) {
        Transaction transaction = transactionRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transação não encontrada."));

        Account account = transaction.getAccount();
        account.setBalance(account.getBalance().add(transaction.getAmount()));
        accountRepo.save(account);

        transactionRepo.delete(transaction);
    }

    private Map<String, Object> mapToSimpleResponse(Transaction t) {
        return Map.of(
            "id", t.getId(),
            "account", t.getAccount().getName(),
            "category", t.getCategory().getName(),
            "amount", t.getAmount(),
            "date", t.getTransactionDate(),
            "description", t.getDescription()
        );
    }
}