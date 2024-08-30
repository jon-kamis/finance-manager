package com.kamis.financemanager.database.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import com.kamis.financemanager.enums.PaymentFrequencyEnum;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name="loans")
public class Loan {

	@Id
	@SequenceGenerator(name="loans_id_seq", sequenceName="loans_id_seq", allocationSize = 1)
	@GeneratedValue(generator="loans_id_seq", strategy = GenerationType.SEQUENCE)
	@Column(name = "id", insertable = false, updatable = false)
	private Integer id;
	
	@Column(name="user_id")
	private Integer userId;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", insertable = false, updatable = false)
	private User user;
	
	@Column(name="account_name")
	private String name;
	
	@Column(name="first_payment_dt")
	private Date firstPaymentDate;
	
	@Column(name="principal")
	private Float principal;
	
	@Column(name="interest")
	private Float interest;
	
	@Column(name="payment")
	private Float payment;
	
	@Column(name="balance")
	private Float balance;

	@Column(name="current_payment_number")
	private Integer currentPaymentNumber;
	
	@Column(name="rate")
	private Float rate;
	
	@Column(name="term")
	private Integer term;
	
    @Column(name="frequency")
	private PaymentFrequencyEnum frequency;
	
	@OneToMany(mappedBy = "loan", fetch = FetchType.EAGER, orphanRemoval = true, cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REMOVE})
	private List<LoanPayment> payments;

	@OneToMany(mappedBy = "loan", fetch = FetchType.EAGER, orphanRemoval = true, cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REMOVE})
	private List<LoanManualPayment> manualPayments;
	
	@Embedded
	private AuditInfo auditInfo;
	
	/**
	 * Adds a LoanPayment to this loan
	 * @param payment The LoanPayment to add
	 */
	public void addLoanPayment(LoanPayment payment) {
		
		if (this.payments == null) {
			this.payments = new ArrayList<>();
		}
		
		payment.setLoan(this);
		this.payments.add(payment);
	}

	/**
	 * Adds a LoanManualPayment to this loan
	 * @param payment The LoanManualPayment to add
	 */
	public void addManualLoanPayment(LoanManualPayment payment) {

		if (this.manualPayments == null) {
			this.manualPayments = new ArrayList<>();
		}

		payment.setLoan(this);
		this.manualPayments.add(payment);
	}

	/**
	 * Adds a list of LoanManualPayments to this loan
	 * @param newPayments The list of manual payments to add
	 */
	public void addAllLoanManualPayments(List<LoanManualPayment> newPayments) {
		for (LoanManualPayment p : newPayments) {
			addManualLoanPayment(p);
		}
	}

	/**
	 * Adds a list of LoanPayments to this loan
	 * @param newPayments The list of payments to add
	 */
	public void addAllLoanPayments(List<LoanPayment> newPayments) {
		for (LoanPayment p : newPayments) {
			addLoanPayment(p);
		}
	}

	/**
	 * Removes a manual payment
	 * @param payment The manual payment to remove
	 */
	public void removeManualPayment(LoanManualPayment payment) {
		this.getManualPayments().removeIf(p -> Objects.equals(p.getId(), payment.getId()));
	}

	/**
	 * Removes a payment
	 * @param payment The payment to remove
	 */
	public void removePayment(LoanPayment payment) {
		this.getPayments().removeIf(p -> Objects.equals(p.getId(), payment.getId()));
	}

	/**
	 * Clears all payments
	 */
	public void clearPayments() {
		this.payments.clear();
	}

	/**
	 * Clears all manual payments
	 */
	public void clearManualPayments() {
		this.manualPayments.clear();
	}

	/**
	 * Removes all manual Payments in the list
	 * @param payments The payments to remove
	 */
	public void removeAllManualPayments(List<LoanManualPayment> payments) {
		for(LoanManualPayment p : payments) {
			this.removeManualPayment(p);
		}
	}

	/**
	 * Removes all Payments in the list
	 * @param payments The payments to remove
	 */
	public void removeAllPayments(List<LoanPayment> payments) {
		for(LoanPayment p : payments) {
			this.removePayment(p);
		}
	}
}
