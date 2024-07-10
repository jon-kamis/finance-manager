package com.kamis.financemanager.database.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name="roles")
public class Role {

	@Id
	@SequenceGenerator(name="roles_id_seq", sequenceName="roles_id_seq", allocationSize = 1)
	@GeneratedValue(generator="roles_id_seq", strategy = GenerationType.SEQUENCE)
	@Column(name = "id", updatable = false)
	private Integer id;
	
	@Column(name="role_name")
	private String name;
	
	@Embedded
	private AuditInfo auditInfo;
}
