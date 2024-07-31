package com.kamis.financemanager.database.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

import com.kamis.financemanager.database.domain.StandardWithholding;

public interface StandardWithholdingRepository extends JpaRepository<StandardWithholding, Integer> {

	/**
	 * Attempts to find a single StandardWithholding based on a specification
	 * @param spec The specification to search with
	 * @return An Optional StandardWithholding matching spec's criteria if one is found
	 */
	public Optional<StandardWithholding> findOne(Specification<StandardWithholding> spec);
	
	/**
	 * Finds all StandardWithholdings based on a specification
	 * @param spec The specification to search with
	 * @return A list of StandardWithholdings matching spec's criteria if one is found
	 */
	public List<StandardWithholding> findAll(Specification<StandardWithholding> spec);
	
	/**
	 * Finds all StandardWithholdings based on a specification
	 * @param spec The specification to search with
	 * @param pageable Pagination Parameters
	 * @return A list of StandardWithholdings matching spec's criteria if one is found
	 */
	public List<StandardWithholding> findAll(Specification<StandardWithholding> spec, Pageable pageable);
	
	/**
	 * Finds all StandardWithholdings based on a specification
	 * @param spec The specification to search with
	 * @param sort Sorting parameters
	 * @return A list of StandardWithholdings matching spec's criteria if one is found
	 */
	public List<StandardWithholding> findAll(Specification<StandardWithholding> spec, Sort sort);
	
	/**
	 * Counts all StandardWithholdings based on a specification
	 * @param spec The specification to search with
	 * @return A count of StandardWithholdings matching spec's criteria if one is found
	 */
	public int count(Specification<StandardWithholding> spec);
}
