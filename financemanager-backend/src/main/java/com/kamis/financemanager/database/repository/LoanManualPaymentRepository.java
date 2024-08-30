package com.kamis.financemanager.database.repository;

import com.kamis.financemanager.database.domain.LoanManualPayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface LoanManualPaymentRepository extends JpaRepository<LoanManualPayment, Integer>, JpaSpecificationExecutor<LoanManualPayment> {
}
