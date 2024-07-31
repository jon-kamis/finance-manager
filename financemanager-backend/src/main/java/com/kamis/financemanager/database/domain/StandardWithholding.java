package com.kamis.financemanager.database.domain;

import com.kamis.financemanager.enums.FilingTypeEnum;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "standard_withholdings")
public class StandardWithholding {
	
	@Id
	@SequenceGenerator(name = "standard_withholdings_id_seq", sequenceName = "standard_withholdings_id_seq", allocationSize = 1)
	@GeneratedValue(generator = "standard_withholdings_id_seq", strategy = GenerationType.SEQUENCE)
	@Column(name = "id", insertable = false, updatable = false)
	private Integer id;
	
	@Column(name = "filing_type")
	private FilingTypeEnum filingType;
	
	@Column(name = "min")
	private Float min;

	@Column(name = "max")
	private Float max;
	
	@Column(name = "amount")
	private Float baseAmount;
	
	@Column(name = "percentage")
	private Float percentage;
}
