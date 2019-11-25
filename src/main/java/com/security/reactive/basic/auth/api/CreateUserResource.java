package com.security.reactive.basic.auth.api;

import java.util.List;
import java.util.UUID;

import com.security.reactive.basic.auth.common.Role;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author Subhakar Kotta
 */
@Getter
@Setter
@NoArgsConstructor
public class CreateUserResource extends UserResource {
	private String password;

	public CreateUserResource(
			UUID id, 
			String email, 
			String password, 
			String firstName, 
			String lastName,
			List<Role> roles) {
		super(id, email, firstName, lastName, roles);
		this.password = password;
	}
}
