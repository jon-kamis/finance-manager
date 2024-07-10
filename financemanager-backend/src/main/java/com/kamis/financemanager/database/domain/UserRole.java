package com.kamis.financemanager.database.domain;

import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name="user_roles")
public class UserRole {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int id;
	
	@OneToOne
	@JoinColumn(name = "user_id", insertable = false, updatable = false)
	private User user;
	
	@OneToOne
	@JoinColumn(name = "role_id", insertable = false, updatable = false)
	private Role role;
	
	@Embedded
	private AuditInfo auditInfo;
}
