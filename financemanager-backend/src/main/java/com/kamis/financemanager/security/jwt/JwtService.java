package com.kamis.financemanager.security.jwt;

import java.util.*;
import java.util.stream.Collectors;

import javax.crypto.SecretKey;

import com.kamis.financemanager.database.domain.RefreshToken;
import com.kamis.financemanager.database.repository.RefreshTokenRepository;
import com.kamis.financemanager.rest.domain.auth.JwtResponse;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import com.kamis.financemanager.config.YAMLConfig;
import com.kamis.financemanager.constants.FinanceManagerConstants;
import com.kamis.financemanager.database.domain.User;
import com.kamis.financemanager.database.repository.UserRepository;
import com.kamis.financemanager.exception.FinanceManagerException;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.impl.lang.Function;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class JwtService {
	
	@Autowired
	private YAMLConfig myConfig;
	
	@Autowired
	private UserRepository userRepository;

	@Autowired
	private RefreshTokenRepository refreshTokenRepository;

	@Transactional
	public String generateToken(String userName) throws FinanceManagerException{
		Map<String, Object> claims = new HashMap<>();
		Optional<User> user = userRepository.findByUsername(userName);
		
		if (user.isEmpty()) {
			log.error("failed to find user for JWT Token generation");
			throw new FinanceManagerException(myConfig.getGenericInternalServerErrorMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		claims.put(FinanceManagerConstants.JWT_CLAIM_USER_ID, user.get().getId());
		claims.put(FinanceManagerConstants.JWT_CLAIM_USER_DISPLAY_NAME, user.get().getLastName() + ", " + user.get().getFirstName());
		claims.put(FinanceManagerConstants.JWT_CLAIM_USER_ROLES, user.get().getUserRoles().stream().map(ur -> ur.getRole().getName()).collect(Collectors.joining(",")));
		
		return createToken(claims, userName);
	}

	private String createToken(Map<String, Object> claims, String userName) {
		return Jwts.builder().claims(claims).subject(userName).issuedAt(new Date(System.currentTimeMillis()))
				.expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 15)).signWith(getSignKey()).compact();
	}

	private SecretKey getSignKey() {
		byte[] keyBytes = Decoders.BASE64.decode(myConfig.getJwtSecret());
		return Keys.hmacShaKeyFor(keyBytes);
	}

	public String extractUsername(String token) {
		return extractClaim(token, Claims::getSubject);
	}

	public Date extractExpiration(String token) {
		return extractClaim(token, Claims::getExpiration);
	}

	public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
		final Claims claims = extractAllClaims(token);
		return claimsResolver.apply(claims);
	}

	private Claims extractAllClaims(String token) { 
	        return Jwts.parser()
	                .verifyWith(getSignKey()) 
	                .build() 
	                .parseSignedClaims(token) 
	                .getPayload(); 
	    }

	private Boolean isTokenExpired(String token) {
		return extractExpiration(token).before(new Date());
	}

	public Boolean validateToken(String token, User user) {
		final String username = extractUsername(token);
		return (username.equals(user.getUsername()) && !isTokenExpired(token));
	}

	/**
	 * Generates a Refresh UUID token
	 * @param username The  username to generate the token for
	 * @return a new UUID refresh token
	 */
	@Transactional
	public UUID generateRefreshToken(String username) {
		RefreshToken refreshToken = new RefreshToken();
		refreshToken.setUsername(username);
		refreshToken.setExpirationDate(new Date(System.currentTimeMillis() + 1000 * 60 * 20));
		refreshToken.setToken(UUID.randomUUID());

		//Remove existing refresh tokens for this user
		refreshTokenRepository.deleteByUsername(username);
		refreshTokenRepository.flush();

		refreshToken = refreshTokenRepository.save(refreshToken);

		return refreshToken.getToken();
	}

	/**
	 * Validates a refresh token
	 * @param token The refresh token to validate
	 * @return true if the token is valid, false otherwise
	 */
	public boolean validateRefreshToken(UUID token) {
		Optional<RefreshToken> refreshToken = refreshTokenRepository.findByToken(token);

        return refreshToken.isPresent() && refreshToken.get().getExpirationDate().compareTo(new Date()) >= 0;
    }

	@Transactional
	public JwtResponse generateTokensFromRefreshToken(UUID token) throws FinanceManagerException {
		JwtResponse response = new JwtResponse();
		Optional<RefreshToken> refreshToken = refreshTokenRepository.findByToken(token);

		if (refreshToken.isEmpty()) {
			throw new FinanceManagerException(myConfig.getRefreshTokenDoesNotExistErrorMsg(), HttpStatus.UNAUTHORIZED);
		}

		response.setRefreshToken(generateRefreshToken(refreshToken.get().getUsername()));
		response.setAccessToken(generateToken(refreshToken.get().getUsername()));

		return response;
	}
}
