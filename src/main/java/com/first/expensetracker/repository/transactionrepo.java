package com.first.expensetracker.repository;

import com.first.expensetracker.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public interface transactionrepo extends JpaRepository<Transaction, UUID> {

    // COALESCE garante que, se não houver nenhuma transação no mês, retorne 0 em vez de null.
    @Query("""
        SELECT COALESCE(SUM(t.amount), 0) 
        FROM Transaction t 
        WHERE t.category.id = :categoryId 
        AND t.transactionDate BETWEEN :startDate AND :endDate
    """)
    BigDecimal sumByCategoryIdAndDateBetween(
            @Param("categoryId") UUID categoryId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );
}