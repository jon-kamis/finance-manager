package com.kamis.financemanager.rest.domain.auth;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class JwtResponse {

	private String accessToken;

	public JwtResponse(String accessToken) {
		this.accessToken = accessToken;
	}
}
