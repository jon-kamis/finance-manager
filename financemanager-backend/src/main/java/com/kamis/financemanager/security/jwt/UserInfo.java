package com.kamis.financemanager.security.jwt;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.kamis.financemanager.database.domain.User;
import com.kamis.financemanager.database.domain.UserRole;

/**
 * UserInfo implements UserDetails for use with Spring Web Security
 */
public class UserInfo implements UserDetails {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String name;
	private String password;
	private List<GrantedAuthority> authorities;
	
	public UserInfo(User user, List<UserRole> roles) {
		name = user.getUsername();
		password = user.getPassword();
		authorities = roles.stream()
				.map(UserRole::getRole)
				.map(r -> r.getName())
				.map(SimpleGrantedAuthority::new)
				.collect(Collectors.toList());
	}
	
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authorities;
	}

	@Override
	public String getPassword() {
		return password;
	}

	@Override
	public String getUsername() {
		return name;
	}

}
