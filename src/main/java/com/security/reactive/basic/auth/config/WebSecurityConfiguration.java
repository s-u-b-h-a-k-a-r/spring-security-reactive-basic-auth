package com.security.reactive.basic.auth.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.autoconfigure.security.reactive.EndpointRequest;
import org.springframework.boot.autoconfigure.security.reactive.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;

import com.security.reactive.basic.auth.common.Role;
import com.security.reactive.basic.auth.service.AuthReactiveUserDetailsService;

@EnableWebFluxSecurity
public class WebSecurityConfiguration {
	@Autowired
	private AuthReactiveUserDetailsService userDetailsService;

	@Bean
	public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
		return http.csrf()
				.disable()
				.authorizeExchange()
				.matchers(PathRequest.toStaticResources().atCommonLocations())
				.permitAll()
				.matchers(EndpointRequest.to("health"))
				.permitAll()
				.matchers(EndpointRequest.to("info"))
				.permitAll()
				.pathMatchers(HttpMethod.GET, "/users/{userId}")
				.hasRole(Role.NORMAL_USER.name())
				.pathMatchers("/users/**")
				.hasRole(Role.ADMIN_USER.name())
				.anyExchange()
				.authenticated()
				.and()
				.httpBasic()
				.and()
				.formLogin()
				.and()
				.logout()
				.and()
				.build();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public ReactiveAuthenticationManager authenticationManager() {
		UserDetailsRepositoryReactiveAuthenticationManager authenticationManager = new UserDetailsRepositoryReactiveAuthenticationManager(
				userDetailsService);
		authenticationManager.setPasswordEncoder(passwordEncoder());
		return authenticationManager;
	}
}
