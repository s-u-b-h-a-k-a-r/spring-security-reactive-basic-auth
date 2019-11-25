package com.security.reactive.basic.auth.dataaccess;

import java.util.List;
import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.security.reactive.basic.auth.common.Role;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Document(collection = "users")
@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
public class User {
	@Id
	private UUID id;
	@Indexed
	private String email;
	private String password;
	private String firstName;
	private String lastName;
	private List<Role> roles;

	public User(User user) {
		this(user.getId(), user.getEmail(), user.getPassword(), user.getFirstName(), user.getLastName(),
				user.getRoles());
	}

	@PersistenceConstructor
	public User(UUID id, String email, String password, String firstName, String lastName, List<Role> roles) {
		this.id = id;
		this.email = email;
		this.password = password;
		this.firstName = firstName;
		this.lastName = lastName;
		this.roles = roles;
	}
}
