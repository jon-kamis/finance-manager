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
	
	@Column(name="interest")
	private Float interest;
	
	@Column(name="payment")
	private Float payment;
	
	@Column(name="rate")
	private Float rate;
	
	@Column(name="term")
	private Integer term;
	
	@Embedded
	private AuditInfo auditInfo;
}
