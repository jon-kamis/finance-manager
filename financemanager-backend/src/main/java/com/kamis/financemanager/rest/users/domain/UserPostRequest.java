package com.kamis.financemanager.rest.users.domain;

import lombok.Data;

@Data
public class UserPostRequest {

	private Integer id;
	private String username;
	private String firstName;
	private String lastName;
	private String password;
	private String email;
	
}
