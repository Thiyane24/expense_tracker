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
import java.util.List;
import java.util.UUID;
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
    public TransactionResponse registrarDespesa(TransactionRequest request) {
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

        return mapToResponse(transacaoSalva);
    }

    public List<TransactionResponse> listAllTransactions() {
        return transactionRepo.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public TransactionResponse getTransactionById(UUID id) {
        Transaction transaction = transactionRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transação não encontrada."));
        return mapToResponse(transaction);
    }

    @Transactional
    public TransactionResponse updateTransaction(UUID id, TransactionRequest request) {
        Transaction transaction = transactionRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transação não encontrada."));

        Account account = transaction.getAccount();
        BigDecimal oldAmount = transaction.getAmount();
        BigDecimal newAmount = request.amount();

        // If account is changing, we need to handle balance for both old and new accounts
        if (!account.getId().equals(request.accountId())) {
            Account newAccount = accountRepo.findById(request.accountId())
                    .orElseThrow(() -> new ResourceNotFoundException("Nova conta não encontrada."));

            // Restore old balance
            account.setBalance(account.getBalance().add(oldAmount));
            accountRepo.save(account);

            // Subtract new amount from new account
            newAccount.setBalance(newAccount.getBalance().subtract(newAmount));
            accountRepo.save(newAccount);

            transaction.setAccount(newAccount);
        } else {
            // Same account: just adjust the difference
            // diff = old - new. If old=100, new=40, diff=60 (increase balance)
            BigDecimal diff = oldAmount.subtract(newAmount);
            account.setBalance(account.getBalance().add(diff));
            accountRepo.save(account);
        }

        Category category = categoryRepo.findById(request.categoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Categoria não encontrada."));

        transaction.setCategory(category);
        transaction.setAmount(newAmount);
        transaction.setTransactionDate(request.transactionDate());
        transaction.setDescription(request.description());

        Transaction updatedTransaction = transactionRepo.save(transaction);
        return mapToResponse(updatedTransaction);
    }

    @Transactional
    public void deleteTransaction(UUID id) {
        Transaction transaction = transactionRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transação não encontrada."));

        Account account = transaction.getAccount();
        // Restore the amount to the account balance
        account.setBalance(account.getBalance().add(transaction.getAmount()));
        accountRepo.save(account);

        transactionRepo.delete(transaction);
    }

    private TransactionResponse mapToResponse(Transaction transaction) {
        Category category = transaction.getCategory();
        LocalDate date = transaction.getTransactionDate();
        LocalDate startOfMonth = date.withDayOfMonth(1);
        LocalDate endOfMonth = date.withDayOfMonth(date.lengthOfMonth());

        BigDecimal totalSpentInMonth = transactionRepo.sumByCategoryIdAndDateBetween(
                category.getId(), startOfMonth, endOfMonth
        );

        BigDecimal alertLimit = category.getMonthlyLimit().multiply(new BigDecimal("0.80"));
        boolean nearLimit = totalSpentInMonth.compareTo(alertLimit) >= 0;

        BudgetSummaryResponse budgetStatus = new BudgetSummaryResponse(
                category.getMonthlyLimit(),
                totalSpentInMonth,
                nearLimit
        );

        return new TransactionResponse(
                transaction.getId(),
                transaction.getAccount().getName(),
                category.getName(),
                transaction.getAmount(),
                transaction.getTransactionDate(),
                transaction.getDescription(),
                budgetStatus
        );
    }
}