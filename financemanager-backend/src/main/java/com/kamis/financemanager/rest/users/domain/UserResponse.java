package com.kamis.financemanager.rest.users.domain;

import lombok.Data;

@Data
public class UserResponse {

	private Integer id;
	private String displayName;
	private String username;
	private String firstName;
	private String lastName;
	private String email;
	
}
