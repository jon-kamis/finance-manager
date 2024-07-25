package com.kamis.financemanager.database.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.kamis.financemanager.database.domain.Transaction;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Integer> {

	/**
	 * Returns a list of transactions with the given userId and name
	 * @param userId The userId to search for
	 * @param name The name to search for
	 * @return A List of transactions matching the given criteria
	 */
	public List<Transaction> findByUserIdAndName(int userId, String name);
}
