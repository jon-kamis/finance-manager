package com.kamis.financemanager.database.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "user_roles")
public class UserRole {

	@Id
	@SequenceGenerator(name="user_roles_id_seq", sequenceName="user_roles_id_seq", allocationSize = 1)
	@GeneratedValue(generator="user_roles_id_seq", strategy = GenerationType.SEQUENCE)
	@Column(name = "id", updatable = false)
	private Integer id;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private User user;

	@OneToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "role_id")
	private Role role;
	
	@Column(name = "role_id", insertable = false, updatable = false)
	private Integer roleId;
	
	@Column(name = "user_id", insertable = false, updatable = false)
	private Integer userId;

	@Embedded
	private AuditInfo auditInfo;
}
