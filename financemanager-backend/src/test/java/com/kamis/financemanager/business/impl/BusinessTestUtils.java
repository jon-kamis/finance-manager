package com.kamis.financemanager.business.impl;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.kamis.financemanager.database.domain.AuditInfo;
import com.kamis.financemanager.database.domain.Loan;
import com.kamis.financemanager.database.domain.LoanPayment;
import com.kamis.financemanager.database.domain.Transaction;
import com.kamis.financemanager.database.domain.TransactionDay;
import com.kamis.financemanager.enums.PaymentFrequencyEnum;
import com.kamis.financemanager.enums.TransactionCategoryEnum;
import com.kamis.financemanager.enums.TransactionTypeEnum;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BusinessTestUtils {

	public static Loan mockPaymentsForBalanceTestActive() {

		Loan loan = new Loan();
		loan.setPrincipal((float) 5);

		List<LoanPayment> payments = new ArrayList<>();

		LocalDate date = LocalDate.now().minusDays(1).minusWeeks(3);

		// Add 5 payments starting at 3 weeks and one day ago. This makes the fourth
		// payment the one our test should return

		for (int i = 1; i <= 5; i++) {
			LoanPayment payment = new LoanPayment();
			payment.setPaymentNumber(i);
			payment.setPaymentDate(Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant()));
			payment.setBalance((float) (5 - i));
			payments.add(payment);

			log.info("Adding test loanPayment {}", payment.toString());
			date = date.plusWeeks(1);
		}

		loan.setPayments(payments);

		return loan;
	}

	public static Loan mockPaymentsForBalanceTestFutureLoan() {

		Loan loan = new Loan();
		loan.setPrincipal((float) 5);

		List<LoanPayment> payments = new ArrayList<>();

		LocalDate date = LocalDate.now().plusWeeks(1);

		// Add 5 payments starting one week in the future. This makes no payment active
		// and balance should be returned as null

		for (int i = 1; i <= 5; i++) {
			LoanPayment payment = new LoanPayment();
			payment.setPaymentNumber(i);
			payment.setPaymentDate(Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant()));
			payment.setBalance((float) (5 - i));
			payments.add(payment);

			log.info("Adding test loanPayment {}", payment.toString());
			date = date.plusWeeks(1);
		}

		loan.setPayments(payments);

		return loan;
	}

	public static Transaction mockBillTransaction(int userId, String name) {

		AuditInfo auditInfo = new AuditInfo();
		auditInfo.setCreateDt(new Date());
		auditInfo.setLastUpdateBy("test");
		auditInfo.setLastUpdateDt(new Date());

		Transaction transaction = new Transaction(null, userId, null, name, PaymentFrequencyEnum.MONTHLY,
				TransactionTypeEnum.EXPENSE, TransactionCategoryEnum.BILL, (float) 50, null, new Date(), null,
				auditInfo);

		TransactionDay day = new TransactionDay(null, null, null, 15, auditInfo);
		transaction.addTransactionDay(day);

		return transaction;
	}

	public static Transaction mockIncomeTransaction(int userId, String name) {

		AuditInfo auditInfo = new AuditInfo();
		auditInfo.setCreateDt(new Date());
		auditInfo.setLastUpdateBy("test");
		auditInfo.setLastUpdateDt(new Date());

		Transaction transaction = new Transaction(null, userId, null, name, PaymentFrequencyEnum.MONTHLY,
				TransactionTypeEnum.INCOME, TransactionCategoryEnum.PAYCHECK, (float)5000, null, new Date(), null,
				auditInfo);

		TransactionDay day = new TransactionDay(null, null, null, 15, auditInfo);
		transaction.addTransactionDay(day);

		return transaction;
	}
	
	public static Transaction mockBenefitTransaction(int userId, String name) {

		AuditInfo auditInfo = new AuditInfo();
		auditInfo.setCreateDt(new Date());
		auditInfo.setLastUpdateBy("test");
		auditInfo.setLastUpdateDt(new Date());

		Transaction transaction = new Transaction(null, userId, null, name, PaymentFrequencyEnum.MONTHLY,
				TransactionTypeEnum.EXPENSE, TransactionCategoryEnum.BENEFIT, (float)100, null, new Date(), null,
				auditInfo);

		TransactionDay day = new TransactionDay(null, null, null, 15, auditInfo);
		transaction.addTransactionDay(day);

		return transaction;
	}

	public static Transaction mockBenefitTransaction(Integer userId, String name, TransactionCategoryEnum cat,
			TransactionTypeEnum type, float amount) {
		
		AuditInfo auditInfo = new AuditInfo();
		auditInfo.setCreateDt(new Date());
		auditInfo.setLastUpdateBy("test");
		auditInfo.setLastUpdateDt(new Date());

		Transaction transaction = new Transaction(null, userId, null, name, PaymentFrequencyEnum.MONTHLY,
				type, cat, amount, null, new Date(), null,
				auditInfo);

		TransactionDay day = new TransactionDay(null, null, null, 15, auditInfo);
		transaction.addTransactionDay(day);

		return transaction;
	}
}
