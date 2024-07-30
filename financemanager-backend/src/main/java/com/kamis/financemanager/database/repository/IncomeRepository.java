package com.kamis.financemanager.database.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

import com.kamis.financemanager.database.domain.Income;

public interface IncomeRepository extends JpaRepository<Income, Integer> {

	/**
	 * Finds a single matching a specification
	 * @param spec The specification to search with
	 * @return An Optional Income matching the criteria of spec if present
	 */
	public Optional<Income> findOne(Specification<Income> spec);
	
	/**
	 * Finds all records matching a specification
	 * @param spec The specification to search with
	 * @param pageable Pagination parameters
	 * @return An Optional Income matching the criteria of spec if present
	 */
	public List<Income> findAll(Specification<Income> spec, Pageable pageable);
	
	/**
	 * Finds all records matching a specification
	 * @param spec The specification to search with
	 * @param sort Sorting parameters
	 * @return An Optional Income matching the criteria of spec if present
	 */
	public List<Income> findAll(Specification<Income> spec, Sort sort);
	
	/**
	 * counts all records matching a specification
	 * @param spec The specification to search with
	 * @return The number of matching records
	 */
	public int count(Specification<Income> spec);


}
