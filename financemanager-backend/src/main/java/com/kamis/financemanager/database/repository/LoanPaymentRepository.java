package com.kamis.financemanager.database.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.kamis.financemanager.database.domain.LoanPayment;

@Repository
public interface LoanPaymentRepository extends JpaRepository<LoanPayment, Integer> {
	
	/**
	 * Deletes all loan payments associated with a given loan
	 * @param loanId The id of the loan to delete payments for
	 */
	public void deleteByLoanId(int loanId);
}
