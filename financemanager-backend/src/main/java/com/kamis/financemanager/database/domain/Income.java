package com.kamis.financemanager.database.domain;

import java.util.Date;

import com.kamis.financemanager.enums.FilingTypeEnum;
import com.kamis.financemanager.enums.PaymentFrequencyEnum;
import com.kamis.financemanager.enums.TransactionCategoryEnum;

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
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "incomes")
public class Income {

	@Id
	@SequenceGenerator(name = "incomes_id_seq", sequenceName = "incomes_id_seq", allocationSize = 1)
	@GeneratedValue(generator = "incomes_id_seq", strategy = GenerationType.SEQUENCE)
	@Column(name = "id", insertable = false, updatable = false)
	private Integer id;

	@Column(name = "user_id")
	private Integer userId;
	
	@Column(name = "income_name")
	private String name;

	@Column(name = "withheld_tax")
	private Float withheldTax;
	
	@Column(name = "tax_credits")
	private Integer taxCredits;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", insertable = false, updatable = false)
	private User user;
	
	@Column(name = "frequency")
	private PaymentFrequencyEnum frequency;
	
	@Column(name = "category")
	private TransactionCategoryEnum category;
	
	@Column(name = "amount")
	private Float amount;
	
	@Column(name = "taxable")
	private Boolean taxable;
	
	@Column(name = "filing_type")
	private FilingTypeEnum filingType;
	
	@Column(name = "effective_dt")
	private Date effectiveDate;
	
	@Column(name = "expiration_dt")
	private Date expirationDate;
	
	@Embedded
	private AuditInfo auditInfo;
}
