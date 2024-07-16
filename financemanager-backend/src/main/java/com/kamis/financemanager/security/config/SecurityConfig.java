package com.kamis.financemanager.security.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import com.kamis.financemanager.config.YAMLConfig;
import com.kamis.financemanager.database.repository.UserRepository;
import com.kamis.financemanager.database.repository.UserRoleRepository;
import com.kamis.financemanager.security.SecurityService;
import com.kamis.financemanager.security.jwt.JwtAuthFilter;
import com.kamis.financemanager.security.jwt.JwtService;

import jakarta.servlet.http.HttpServletResponse;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	public SecurityConfig() {
	}

	@Autowired
	private YAMLConfig myConfig;
	
	@Autowired
	private JwtService jwtService;

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private UserRoleRepository userRoleRepository;

	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	@Bean 
	public SecurityService securityService() {
		return new SecurityService(userRepository, myConfig);
	}

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
			throws Exception {
		return authenticationConfiguration.getAuthenticationManager();
	}

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http.cors(Customizer.withDefaults());
		http.csrf(csrf -> csrf.disable());
		http.sessionManagement(sessionManager -> sessionManager.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
		http.exceptionHandling(
				exceptionHandling -> exceptionHandling.authenticationEntryPoint((request, response, exception) -> {
					response.sendError(HttpServletResponse.SC_UNAUTHORIZED, exception.getMessage());
				}));
		http.authorizeHttpRequests(authorizeHttpRequest -> authorizeHttpRequest
				.requestMatchers("/actuator/health", "/actuator/metrics", "/actuator/metrics/**", "/v3/api-docs/**",
						"/swagger-ui/**", "/swagger-ui.html", "/api/monitor/**", "/auth/**")
				.permitAll()
				.anyRequest().authenticated());

		http.addFilterBefore(new JwtAuthFilter(jwtService, userRepository, userRoleRepository), UsernamePasswordAuthenticationFilter.class); // custom
																															// protocol
																															// Authorization
		return http.build();
	}

	@Bean
	public CorsFilter corsFilter() {
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		CorsConfiguration config = new CorsConfiguration();
		config.setAllowCredentials(true);
		config.addAllowedHeader("*");
		config.addAllowedMethod("*");
		source.registerCorsConfiguration("/**", config);
		return new CorsFilter(source);
	}
}
