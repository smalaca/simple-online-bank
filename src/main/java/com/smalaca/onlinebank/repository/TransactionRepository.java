package com.smalaca.onlinebank.repository;

import com.smalaca.onlinebank.domain.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TransactionRepository extends JpaRepository<Transaction, UUID> {}
