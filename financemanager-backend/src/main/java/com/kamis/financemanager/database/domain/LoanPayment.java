package com.kamis.financemanager.database.domain;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name="loan_payments")
public class LoanPayment {

	@Id
	@SequenceGenerator(name="loan_payments_id_seq", sequenceName="loan_payments_id_seq", allocationSize = 1)
	@GeneratedValue(generator="loan_payments_id_seq", strategy = GenerationType.SEQUENCE)
	@Column(name = "id", insertable = false, updatable = false)
	private Integer id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "loan_id")
	private Loan loan;
	
	@Column(name="payment_dt")
	private Date paymentDate;
	
	@Column(name="principal")
	private Float principal;
	
	@Column(name="principal_to_date")
	private Float principalToDate;
	
	@Column(name="interest")
	private Float interest;
	
	@Column(name="interest_to_date")
	private Float interestToDate;
	
	@Column(name="amount")
	private Float amount;
	
	@Column(name="payment_number")
	private Integer paymentNumber;

	@Column(name="balance")
	private Float balance;

	@Column(name="is_manual")
	private boolean manualPayment;

	@Embedded
	private AuditInfo auditInfo;
}
