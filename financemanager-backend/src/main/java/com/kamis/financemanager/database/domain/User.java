package com.kamis.financemanager.database.domain;

import java.util.ArrayList;
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
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name="users")
public class User {

	@Id
	@SequenceGenerator(name="users_id_seq", sequenceName="users_id_seq", allocationSize = 1)
	@GeneratedValue(generator="users_id_seq", strategy = GenerationType.SEQUENCE)
	@Column(name = "id", insertable = false, updatable = false)
	private Integer id;
	
	@Column(name="first_name")
	private String firstName;
	
	@Column(name="last_name")
	private String lastName;
	
	@Column(name="username")
	private String username;
	
	@Column(name="password")
	private String password;
	
	@Column(name="email")
	private String email;
	
	@OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
	private List<UserRole> userRoles;
	
	@OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
	private List<Loan> loans;
	
	@Embedded
	private AuditInfo auditInfo;
	
	/**
	 * Adds a new role to the user by creating a new userRole object
	 * @param role The role to add
	 * @param username the username of the user adding this role
	 */
	public void addRole(Role role, String username){
		if (userRoles == null) {
			userRoles = new ArrayList<>();
		} else if (userRoles.stream().anyMatch(r -> r.getRole().getId() == role.getId())) {
			//Skip adding role if user already has it
			return;
		}
		
		AuditInfo auditInfo = new AuditInfo();
		auditInfo.setCreateDt(new Date(System.currentTimeMillis()));
		auditInfo.setLastUpdateDt(new Date(System.currentTimeMillis()));
		auditInfo.setLastUpdateBy(username);
		
		UserRole userRole = new UserRole();
		userRole.setRole(role);
		userRole.setUser(this);
		userRole.setAuditInfo(auditInfo);
		
		userRoles.add(userRole);
	}
}
