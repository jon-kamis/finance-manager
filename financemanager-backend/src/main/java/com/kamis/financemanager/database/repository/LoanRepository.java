package com.kamis.financemanager.database.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.kamis.financemanager.database.domain.Loan;

@Repository
public interface LoanRepository extends JpaRepository<Loan, Integer> {
	
}
