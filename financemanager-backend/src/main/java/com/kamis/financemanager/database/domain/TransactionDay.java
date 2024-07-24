package com.kamis.financemanager.database.domain;

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
@Table(name="transaction_days")
public class TransactionDay {

	@Id
	@SequenceGenerator(name="transaction_days_id_seq", sequenceName="transactions_id_seq", allocationSize = 1)
	@GeneratedValue(generator="transaction_days_id_seq", strategy = GenerationType.SEQUENCE)
	@Column(name = "id", updatable = false)
	private Integer id;
	
	@Column(name="user_id", insertable = false, updatable = false)
	private Integer userId;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "transaction_id")
	private Transaction transaction;
	
	@Column(name="day")
	private Integer day;
	
	@Embedded
	private AuditInfo auditInfo;
}
