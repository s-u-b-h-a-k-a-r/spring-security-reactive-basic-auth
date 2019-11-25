package com.security.reactive.basic.auth.service;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;

import com.security.reactive.basic.auth.dataaccess.User;

import java.util.Collection;
import java.util.stream.Collectors;

@SuppressWarnings("serial")
public class AuthUser extends User implements UserDetails {

	public AuthUser(User user) {
		super(user);
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return AuthorityUtils.commaSeparatedStringToAuthorityList(
				getRoles().stream().map(rn -> "ROLE_" + rn.name()).collect(Collectors.joining(",")));
	}

	@Override
	public String getUsername() {
		return getEmail();
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}
}
