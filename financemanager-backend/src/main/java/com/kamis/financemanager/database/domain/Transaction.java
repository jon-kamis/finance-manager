package com.kamis.financemanager.database.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.kamis.financemanager.enums.PaymentFrequencyEnum;
import com.kamis.financemanager.enums.TableNameEnum;
import com.kamis.financemanager.enums.TransactionCategoryEnum;
import com.kamis.financemanager.enums.TransactionTypeEnum;

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
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="transactions")
public class Transaction {

	@Id
	@SequenceGenerator(name="transactions_id_seq", sequenceName="transactions_id_seq", allocationSize = 1)
	@GeneratedValue(generator="transactions_id_seq", strategy = GenerationType.SEQUENCE)
	@Column(name = "id", updatable = false)
	private Integer id;
	
	@Column(name="user_id")
	private Integer userId;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", insertable = false, updatable = false)
	private User user;
	
	@Column(name = "transaction_name")
	private String name;
	
    @Column(name="frequency")
	private PaymentFrequencyEnum frequency;
	
    @Column(name="transaction_type")
	private TransactionTypeEnum type;
	
    @Column(name="category")
	private TransactionCategoryEnum category;
	
	@Column(name="amount")
	private Float amount;
	
	@OneToMany(mappedBy = "transaction", fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
	private List<TransactionDay> transactionDays;
	
	@Column(name="parent_table_name")
	private TableNameEnum parentTableName;
	
	@Column(name="parent_id")
	private Integer parentId;
	
	@Column(name="effective_dt")
	private Date effectiveDate;
	
	@Column(name="expiration_dt")
	private Date expirationDate;
	
	@Embedded
	private AuditInfo auditInfo;
	
	
	public void addTransactionDay(TransactionDay transactionDay){
	
		//Check if array needs initialized
		if (transactionDays == null) {
			transactionDays = new ArrayList<>();
		}
	
		transactionDay.setTransaction(this);
		transactionDays.add(transactionDay);
	}
}
