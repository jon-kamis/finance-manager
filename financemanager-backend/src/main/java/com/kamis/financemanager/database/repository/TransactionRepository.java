package com.kamis.financemanager.database.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kamis.financemanager.database.domain.Transaction;

public interface TransactionRepository extends JpaRepository<Transaction, Integer> {

}
