package com.security.reactive.basic.auth.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.ReactiveUserDetailsPasswordService;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class AuthReactiveUserDetailsService implements ReactiveUserDetailsService, ReactiveUserDetailsPasswordService {

	private static final Logger LOGGER = LoggerFactory.getLogger(AuthReactiveUserDetailsService.class);

	private final UserService userService;

	public AuthReactiveUserDetailsService(UserService userService) {
		this.userService = userService;
	}

	@Override
	public Mono<UserDetails> findByUsername(String username) {
		LOGGER.info("Finding user for user name {}", username);
		return userService.findOneByEmail(username).map(AuthUser::new);
	}

	@Override
	public Mono<UserDetails> updatePassword(UserDetails user, String newPassword) {
		LOGGER.warn("Password upgrade for user with name '{}'", user.getUsername());
		// Only for demo purposes. NEVER log passwords in production!!!
		LOGGER.info("Password upgraded from '{}' to '{}'", user.getPassword(), newPassword);
		return userService
				.findOneByEmail(user.getUsername())
					.doOnSuccess(u -> u.setPassword(newPassword))
					.flatMap(userService::update).map(AuthUser::new);
	}
}
