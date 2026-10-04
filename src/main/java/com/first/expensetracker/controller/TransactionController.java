package com.first.expensetracker.controller;

import com.first.expensetracker.dto.request.TransactionRequest;
import com.first.expensetracker.dto.request.response.TransactionResponse;
import com.first.expensetracker.service.TransactionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/transactions")
@CrossOrigin(origins = "*")
public class TransactionController {
    @Autowired
    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping
    public ResponseEntity<TransactionResponse> criarDespesa(@Valid @RequestBody TransactionRequest request) {
        TransactionResponse response = transactionService.registrarDespesa(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}