package com.security.reactive.basic.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.security.reactive.basic.auth.api.CreateUserResource;
import com.security.reactive.basic.auth.api.UserHandler;
import com.security.reactive.basic.auth.api.UserResource;
import com.security.reactive.basic.auth.api.UserResourceAssembler;
import com.security.reactive.basic.auth.config.IdGeneratorConfiguration;
import com.security.reactive.basic.auth.config.ModelMapperConfiguration;
import com.security.reactive.basic.auth.config.UserRouter;
import com.security.reactive.basic.auth.dataaccess.User;
import com.security.reactive.basic.auth.service.UserService;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@WebFluxTest
@Import({
  UserRouter.class,
  UserHandler.class,
  UserResourceAssembler.class,
  ModelMapperConfiguration.class,
  IdGeneratorConfiguration.class
})
@WithMockUser
@DisplayName("Verify user api")
class UserApiDocumentationTest {

  @Autowired private ApplicationContext applicationContext;

  private WebTestClient webTestClient;

  @MockBean private UserService userService;

  @BeforeEach
  void setUp() {
    this.webTestClient =
        WebTestClient.bindToApplicationContext(applicationContext)
            .configureClient()
                .baseUrl("http://localhost:9091")
            .build();
  }



  @Test
  @DisplayName("to get list of users")
  void verifyAndDocumentGetUsers() {

    UUID userId = UUID.randomUUID();
    given(userService.findAll()).willReturn(Flux.just(UserBuilder.user().withId(userId).build()));

    webTestClient
        .get()
        .uri("/users")
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody()
        .json(
            "[{\"id\":\""
                + userId
                + "\",\"email\":\"john.doe@example.com\","
                + "\"firstName\":\"John\",\"lastName\":\"Doe\"}]");
       
  }

  @Test
  @DisplayName("to get single user")
  void verifyAndDocumentGetUser() {

    UUID userId = UUID.randomUUID();

    given(userService.findById(userId))
        .willReturn(Mono.just(UserBuilder.user().withId(userId).build()));

    webTestClient
        .get()
        .uri("/users/{userId}", userId)
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody()
        .json(
            "{\"id\":\""
                + userId
                + "\",\"email\":\"john.doe@example.com\","
                + "\"firstName\":\"John\",\"lastName\":\"Doe\","
                + "\"roles\":[\"LIBRARY_USER\"]}");
  }

  
  @DisplayName("to delete a user")
  @Test
  void verifyAndDocumentDeleteUser() {

    UUID userId = UUID.randomUUID();
    given(userService.deleteById(userId)).willReturn(Mono.empty());

    webTestClient
        .mutateWith(csrf())
        .delete()
        .uri("/users/{userId}", userId)
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody();
  }

  @SuppressWarnings("unchecked")
  @Test
  @DisplayName("to create a new user")
  void verifyAndDocumentCreateUser() throws JsonProcessingException {

    UUID userId = UUID.randomUUID();

    User expectedUser = UserBuilder.user().withId(userId).build();

    UserResource userResource =
        new CreateUserResource(
            userId,
            expectedUser.getEmail(),
            expectedUser.getPassword(),
            expectedUser.getFirstName(),
            expectedUser.getLastName(),
            expectedUser.getRoles());

    given(userService.create(any())).willAnswer(i -> Mono.empty());

    webTestClient
        .mutateWith(csrf())
        .post()
        .uri("/users")
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON)
        .body(BodyInserters.fromValue(new ObjectMapper().writeValueAsString(userResource)))
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody();
        
    ArgumentCaptor<Mono> userArg = ArgumentCaptor.forClass(Mono.class);
    verify(userService).create(userArg.capture());

    assertThat(userArg.getValue().block()).isNotNull().isEqualTo(expectedUser);
  }
}
