package com.kamis.financemanager.database.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.kamis.financemanager.database.domain.Transaction;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Integer>, JpaSpecificationExecutor<Transaction> {

	/**
	 * Returns a list of transactions with the given userId and name
	 * @param userId The userId to search for
	 * @param name The name to search for
	 * @return A List of transactions matching the given criteria
	 */
	public List<Transaction> findByUserIdAndName(int userId, String name);
	
	/**
	 * Find all by specification and page results
	 * @param spec The search specification
	 * @param sort Sorting details
	 * @return A list of Transactions matching the specification
	 */
	public List<Transaction> findAll(Specification<Transaction> spec, Sort sort);

	/**
	 * Retrieves all transactions by a user's id
	 * @param userId
	 * @return A list of transactions belonging to the given userId
	 */
	public List<Transaction> findByUserId(Integer userId);

}
