package com.kamis.financemanager.business.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.kamis.financemanager.FinancemanagerApplicationTests;
import com.kamis.financemanager.business.LoanBusiness;
import com.kamis.financemanager.database.domain.Loan;

@ExtendWith(MockitoExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TestLoanBusinessImpl extends FinancemanagerApplicationTests {

	@Mock
	private LoanBusinessImpl loanBusinessMock;

	@Autowired
	private LoanBusiness loanBusinessActual;

	/**
	 * test that an active loan's balance is calculated correctly
	 */
	@Test
	public void testGetActiveLoanBalance() {

		Mockito.when(loanBusinessMock.getLoanBalance(any())).thenCallRealMethod();

		Loan loan = TestUtils.mockPaymentsForBalanceTestActive();

		float balance = loanBusinessMock.getLoanBalance(loan);

		assertEquals((float) 1, balance);

	}

	/**
	 * test that a paid loan's balance is calculated correctly
	 */
	@Test
	public void testGetFutureLoanBalance() {

		Mockito.when(loanBusinessMock.getLoanBalance(any())).thenCallRealMethod();

		Loan loan = TestUtils.mockPaymentsForBalanceTestFutureLoan();

		float balance = loanBusinessMock.getLoanBalance(loan);

		assertEquals(loan.getPrincipal(), balance);

	}

	/**
	 * test that null or empty loan payments list return 0 for balance
	 */
	@Test
	public void testGetNullOrEmptyPaymentsLoanBalance() {

		Mockito.when(loanBusinessMock.getLoanBalance(any())).thenCallRealMethod();

		Loan loan = new Loan();

		float balance = loanBusinessMock.getLoanBalance(null);
		assertEquals((float) 0, balance);

		balance = loanBusinessMock.getLoanBalance(loan);
		assertEquals((float) 0, balance);

		loan.setPayments(new ArrayList<>());

		balance = loanBusinessMock.getLoanBalance(loan);
		assertEquals((float) 0, balance);

	}
}
