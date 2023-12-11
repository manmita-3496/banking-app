package com.example.bank.service;

import com.example.bank.dto.TransactionDto;
import org.springframework.stereotype.Service;


public interface TransactionService {
    void saveTransaction(TransactionDto transaction);
}
