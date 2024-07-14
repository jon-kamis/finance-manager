package com.kamis.financemanager.database.domain;

import java.util.Date;
import java.util.List;

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
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
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
	
	@Column(name="rate")
	private Float rate;
	
	@Column(name="term")
	private Integer term;
	
	@OneToMany(mappedBy = "loan", fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
	private List<LoanPayment> payments;
	
	@Embedded
	private AuditInfo auditInfo;
}
