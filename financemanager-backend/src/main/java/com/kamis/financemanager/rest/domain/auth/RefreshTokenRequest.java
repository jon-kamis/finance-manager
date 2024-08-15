package com.kamis.financemanager.rest.domain.auth;

import lombok.Data;

import java.util.UUID;

@Data
public class RefreshTokenRequest {
    private UUID refreshToken;
}
