package com.kamis.financemanager.rest.domain.auth;

import lombok.Data;

@Data
public class AuthRequest {

	private String username;
	private String password;
}
