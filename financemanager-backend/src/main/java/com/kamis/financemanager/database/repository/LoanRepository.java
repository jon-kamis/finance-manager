package com.kamis.financemanager.database.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.kamis.financemanager.database.domain.Loan;

@Repository
public interface LoanRepository extends JpaRepository<Loan, Integer> {

	/**
	 * Fetches a list of loans by userId and name
	 * @param userId The userId to filter loans by
	 * @param name The name to filter loans by
	 * @return A List of all loans matching the given criteria
	 */
	@Query(name = "select u from User u where u.id = :userId and u.name like %:name%")
	public List<Loan> findByUserIdAndName(Integer userId, String name);
	
	/**
	 * Fetches a list of loans by userId and name
	 * @param userId The userId to filter loans by
	 * @param name The name to filter loans by
	 * @param pageable A pageable containing paging and sorting information
	 * @return A List of all loans matching the given criteria
	 */
	@Query(name = "select u from User u where u.id = :userId and u.name like %:name%")
	public List<Loan> findByUserIdAndName(Integer userId, String name, Pageable pageable);

	/**
	 * Fetches a list of loans by userId
	 * @param userId The userId to filter loans by
	 * @return A List of all loans matching the given criteria
	 */
	public List<Loan> findByUserId(Integer userId);
	
	/**
	 * Fetches a list of loans by userId
	 * @param userId The userId to filter loans by
	 * @param pageable A pageable containing paging and sorting information
	 * @return A List of all loans matching the given criteria
	 */
	public List<Loan> findByUserId(Integer userId, Pageable pageable);

	/**
	 * Counts the number of loans with a given name for a given user
	 * @param userId The userId of loans to search for
	 * @param name The name of loans to search for
	 * @return An int count representing the number of results
	 */
	@Query(name = "select count(u) from User u where u.id = :userId and u.name like %:name%")
	public int countByUserIdAndName(Integer userId, String name);
	
	/**
	 * Counts the number of loans for a given user
	 * @param userId The userId of loans to search for
	 * @return An int count representing the number of results
	 */
	public int countByUserId(Integer userId);
	
}
