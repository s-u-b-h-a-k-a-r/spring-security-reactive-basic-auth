package com.security.reactive.basic.auth;

import java.util.Collections;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.security.reactive.basic.auth.common.Role;
import com.security.reactive.basic.auth.dataaccess.User;
import com.security.reactive.basic.auth.dataaccess.UserRepository;

import reactor.core.publisher.Flux;

/**
 * @author Subhakar Kotta
 */
@Component
public class DataInitializer implements CommandLineRunner {

	private static final UUID USER_IDENTIFIER = UUID.fromString("c47641ee-e63c-4c13-8cd2-1c2490aee0b3");
	private static final UUID ADMIN_IDENTIFIER = UUID.fromString("0d2c04f1-e25f-41b5-b4cd-3566a081200f");

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	@Autowired
	public DataInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
	}

	@Override
	public void run(String... args) {
		createUsers();
	}

	private void createUsers() {
		final Logger logger = LoggerFactory.getLogger(this.getClass());
		userRepository.deleteAll()
				.thenMany(userRepository.saveAll(Flux.just(
						new User(USER_IDENTIFIER, "user@example.com", passwordEncoder.encode("user"), "Normal", "User",
								Collections.singletonList(Role.NORMAL_USER)),
						new User(ADMIN_IDENTIFIER, "admin@example.com", passwordEncoder.encode("admin"), "Admin",
								"User", Collections.singletonList(Role.ADMIN_USER)))))
				.log().then(userRepository.count()).subscribe(c -> logger.info("{} users created", c));
	}
}
