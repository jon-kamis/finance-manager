package com.kamis.financemanager.database.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.kamis.financemanager.database.domain.Income;

@Repository
public interface IncomeRepository extends JpaRepository<Income, Integer>, JpaSpecificationExecutor<Income> {
}
