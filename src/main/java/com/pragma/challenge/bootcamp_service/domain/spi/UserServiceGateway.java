package com.pragma.challenge.bootcamp_service.domain.spi;

import com.pragma.challenge.bootcamp_service.domain.model.User;
import java.util.List;
import reactor.core.publisher.Mono;

public interface UserServiceGateway {
  Mono<Long> getBootcampWithHigherNumberUsers();

  Mono<List<User>> getBootcampUsers(Long bootcampId);
}
