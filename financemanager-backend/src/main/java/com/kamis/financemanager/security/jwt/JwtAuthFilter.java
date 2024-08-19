package com.kamis.financemanager.security.jwt;

import java.io.IOException;
import java.util.Optional;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.kamis.financemanager.database.domain.User;
import com.kamis.financemanager.database.repository.UserRepository;
import com.kamis.financemanager.database.repository.UserRoleRepository;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Slf4j
@Component
public class JwtAuthFilter extends OncePerRequestFilter{

	private final JwtService jwtService;
	
	private final UserRepository userRepository;
	
	private final UserRoleRepository userRoleRepository;
	
	public JwtAuthFilter(JwtService jwtService, UserRepository userRepository, UserRoleRepository userRoleRepository){
		this.jwtService = jwtService;
		this.userRepository = userRepository;
		this.userRoleRepository = userRoleRepository;
	}
	
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		
		String authHeader = request.getHeader("Authorization");
		String token = null;
		String username = null;
		
		if (authHeader != null && authHeader.startsWith("Bearer ")) {
			token = authHeader.substring(7);
			username = jwtService.extractUsername(token);
		}
		
		if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
			Optional<User> optUser = userRepository.findByUsername(username);
			
			if(optUser.isPresent()) {
				User user = optUser.get();
				
				if (jwtService.validateToken(token,  user)) {
					log.info("Successfully authenticated user {}", user.getUsername());
					UserDetails userInfo = new UserInfo(user, userRoleRepository.findByUserId(user.getId())); 
					UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userInfo, null, userInfo.getAuthorities());
					SecurityContextHolder.getContext().setAuthentication(authToken);
				} else {
					log.debug("User {} attempted to access the system with an expired JWT", user.getUsername());
				}
			} else {
				log.debug("User with username {} attempted to access the system but was not found", username);
			}
		} else {
			log.debug("JwtAuthFilter attempted to validate a JWT token but no username was found or the security context was null");
		}
	
		filterChain.doFilter(request, response);
	}
}
