package com.security.reactive.basic.auth.dataaccess;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

import java.util.UUID;

/**
 * @author Subhakar Kotta
 */
public interface UserRepository extends ReactiveMongoRepository<User, UUID> {
	Mono<User> findOneByEmail(String email);
}
