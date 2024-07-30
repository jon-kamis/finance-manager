package com.kamis.financemanager.database.domain;

import java.util.Date;

import com.kamis.financemanager.enums.WeekdayEnum;

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
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name="transaction_days")
public class TransactionDay {

	@Id
	@SequenceGenerator(name="transaction_days_id_seq", sequenceName="transaction_days_id_seq", allocationSize = 1)
	@GeneratedValue(generator="transaction_days_id_seq", strategy = GenerationType.SEQUENCE)
	@Column(name = "id", updatable = false)
	private Integer id;
	
	@Column(name="transaction_id", insertable = false, updatable = false)
	private Integer transactionId;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "transaction_id")
	private Transaction transaction;
	
	@Column(name="weekday")
	private WeekdayEnum weekday;
	
	@Column(name="startDate")
	private Date startDate;
	
	@Column(name="day")
	private Integer day;
	
	@Embedded
	private AuditInfo auditInfo;
}
