package com.security.reactive.basic.auth.api;

import java.util.List;
import java.util.UUID;

import com.security.reactive.basic.auth.common.Role;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class UserResource {
	private UUID id;
	private String email;
	private String firstName;
	private String lastName;
	private List<Role> roles;
}
